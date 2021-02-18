package com.wxshop.shop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.GoodsInfo;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.api.data.RpcOrderGoods;
import com.wxshop.shop.order.generate.Order;
import com.wxshop.shop.entity.GoodsWithNumber;
import com.wxshop.shop.entity.OrderResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.generate.Goods;
import com.wxshop.shop.generate.Shop;
import com.wxshop.shop.mock.MockOrderRpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
public class OrderIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockOrderRpcService mockOrderRpcService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(mockOrderRpcService);
    }

    @Test
    public void canCreateOrder() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();

        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        GoodsInfo goodsInfo2 = new GoodsInfo();

        goodsInfo1.setId(4);
        goodsInfo1.setNumber(3);
        goodsInfo2.setId(5);
        goodsInfo2.setNumber(5);

        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        when(mockOrderRpcService.orderRpcService.createOrder(any(), any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Order order = invocation.getArgument(1);
                order.setId(1234L);
                return order;
            }
        });

        Response<OrderResponse> response = doHttpRequest("/api/v1/order", "POST", orderInfo, loginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });

        Assertions.assertEquals(1234L, response.getData().getId());

        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.PENDING.getName(), response.getData().getStatus());
        Assertions.assertEquals("火星", response.getData().getAddress());
        Assertions.assertEquals(Arrays.asList(4L, 5L),
                response.getData().getGoods().stream().map(Goods::getId).collect(toList())
        );
        Assertions.assertEquals(Arrays.asList(3, 5),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(toList())
        );

        // 现在获取刚刚创建的订单
        RpcOrderGoods mockRpcOrderGoods = new RpcOrderGoods();
        Order order = new Order();
        order.setId(12345L);
        order.setUserId(1L);
        order.setShopId(2L);
        mockRpcOrderGoods.setOrder(order);
        mockRpcOrderGoods.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        when(mockOrderRpcService.orderRpcService.getOrderById(12345L)).thenReturn(mockRpcOrderGoods);

        Response<OrderResponse> getResponse = doHttpRequest("/api/v1/order/12345", "GET", null, loginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });
        Assertions.assertEquals(12345L, getResponse.getData().getId());
        Assertions.assertEquals(2L, getResponse.getData().getShop().getId());
        Assertions.assertEquals(Arrays.asList(4L, 5L),
                getResponse.getData().getGoods().stream().map(GoodsWithNumber::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 5),
                getResponse.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(toList()));
    }

    @Test
    void canRollBackIfDeductFailed() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();

        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        GoodsInfo goodsInfo2 = new GoodsInfo();

        goodsInfo1.setId(4);
        goodsInfo1.setNumber(3);
        goodsInfo2.setId(5);
        goodsInfo2.setNumber(6);

        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        HttpResponse response = doHttpRequest("/api/v1/order", "POST", orderInfo, loginResponse.cookie);

        Assertions.assertEquals(HttpStatus.GONE.value(), response.code);

        // 确保扣库存成功的回滚了
        canCreateOrder();
    }

    @Test
    void canDeleteOrder() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();
        when(mockOrderRpcService.orderRpcService.getOrder(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(mockResponse());
        //获取当前订单
        PageResponse<OrderResponse> orders = doHttpRequest("/api/v1/order?pageNum=1&pageSize=2", "GET", null, loginResponse.cookie)
                .asJsonObject(new TypeReference<PageResponse<OrderResponse>>() {
                });
        Assertions.assertEquals(3, orders.getPageNum());
        Assertions.assertEquals(10, orders.getTotalPage());
        Assertions.assertEquals(2, orders.getPageSize());
        Assertions.assertEquals(Arrays.asList("shop2", "shop2"),
                orders.getData().stream().map(OrderResponse::getShop).map(Shop::getName).collect(toList()));
        Assertions.assertEquals(Arrays.asList("goods3", "goods4"),
                orders.getData().stream()
                        .map(OrderResponse::getGoods)
                        .flatMap(List::stream)
                        .map(Goods::getName)
                        .collect(toList()));

        Assertions.assertEquals(Arrays.asList(5, 3),
                orders.getData().stream()
                        .map(OrderResponse::getGoods)
                        .flatMap(List::stream)
                        .map(GoodsWithNumber::getNumber)
                        .collect(toList()));

        when(mockOrderRpcService.orderRpcService.deleteOrder(100L, 1L))
                .thenReturn(mockRpcOrderGoods(100, 1, 3, 2, 5, DataStatus.DELETED));


        //删除某个订单
        when(mockOrderRpcService.orderRpcService.deleteOrder(100L, 1L))
                .thenReturn(mockRpcOrderGoods(100, 1, 3, 2, 5, DataStatus.DELETED));
        Response<OrderResponse> deletedOrder = doHttpRequest("/api/v1/order/100", "DELETE", null, loginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });
        Assertions.assertEquals(DataStatus.DELETED.getName(), deletedOrder.getData().getStatus());
        Assertions.assertEquals(100, deletedOrder.getData().getId());
        Assertions.assertEquals(1, deletedOrder.getData().getUserId());
        Assertions.assertEquals(1, deletedOrder.getData().getGoods().size());
        Assertions.assertEquals(2, deletedOrder.getData().getGoods().get(0).getShopId());
        Assertions.assertEquals(3, deletedOrder.getData().getGoods().get(0).getId());
        Assertions.assertEquals(5, deletedOrder.getData().getGoods().get(0).getNumber());

        //再次获取订单
        canCreateOrder();
    }

    @Test
    void return404IfOrderNotFound() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();
        Order order = new Order();
        order.setId(123456L);
        HttpResponse response = doHttpRequest("/api/v1/order/123456", "PATCH", order, loginResponse.cookie);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.code);

    }

    @Test
    void canUpdateOrderExpressInfo() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();
        Order orderUpdateRequest = new Order();
        orderUpdateRequest.setId(123456L);
        orderUpdateRequest.setShopId(2L);
        orderUpdateRequest.setExpressCompany("顺丰");
        orderUpdateRequest.setExpressId("SF1234567");

        Order orderInDB = new Order();
        orderInDB.setId(123456L);
        orderInDB.setShopId(2L);

        RpcOrderGoods mockRpcOrderGoods = new RpcOrderGoods();
        mockRpcOrderGoods.setOrder(orderInDB);


        when(mockOrderRpcService.orderRpcService.getOrderById(123456L)).thenReturn(mockRpcOrderGoods);
        when(mockOrderRpcService.orderRpcService.updateOrder(any())).thenReturn(
                mockRpcOrderGoods(123456L, 1L, 3L, 2L, 10, DataStatus.DELIVERED)
        );
        Response<OrderResponse> response = doHttpRequest("/api/v1/order/123456", "PATCH", orderUpdateRequest, loginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });
        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), response.getData().getStatus());
        Assertions.assertEquals(1, response.getData().getGoods().size());
        Assertions.assertEquals(3L, response.getData().getGoods().get(0).getId());
        Assertions.assertEquals(10, response.getData().getGoods().get(0).getNumber());
    }

    @Test
    void canUpdateOrderStatus() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();
        Order orderUpdateRequest = new Order();
        orderUpdateRequest.setId(123456L);
        orderUpdateRequest.setStatus(DataStatus.RECEIVED.getName());

        Order orderInDB = new Order();
        orderInDB.setId(123456L);
        orderInDB.setUserId(1L);
        orderInDB.setShopId(2L);

        RpcOrderGoods mockRpcOrderGoods = new RpcOrderGoods();
        mockRpcOrderGoods.setOrder(orderInDB);

        when(mockOrderRpcService.orderRpcService.getOrderById(123456L)).thenReturn(mockRpcOrderGoods);
        when(mockOrderRpcService.orderRpcService.updateOrder(any())).thenReturn(
                mockRpcOrderGoods(123456L, 1L, 3L, 2L, 10, DataStatus.RECEIVED)
        );
        Response<OrderResponse> response = doHttpRequest("/api/v1/order/123456", "PATCH", orderUpdateRequest, loginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });
        Assertions.assertEquals(1L, response.getData().getUserId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.RECEIVED.getName(), response.getData().getStatus());
        Assertions.assertEquals(1, response.getData().getGoods().size());
        Assertions.assertEquals(3L, response.getData().getGoods().get(0).getId());
        Assertions.assertEquals(10, response.getData().getGoods().get(0).getNumber());
    }


    private PageResponse<RpcOrderGoods> mockResponse() {
        RpcOrderGoods order1 = mockRpcOrderGoods(100, 1, 3, 2, 5, DataStatus.DELIVERED);
        RpcOrderGoods order2 = mockRpcOrderGoods(101, 1, 4, 2, 3, DataStatus.RECEIVED);

        return PageResponse.pagedData(3, 2, 10, Arrays.asList(order1, order2));
    }

    private RpcOrderGoods mockRpcOrderGoods(long orderId,
                                            long userId,
                                            long goodsId,
                                            long shopId,
                                            int number,
                                            DataStatus status) {
        RpcOrderGoods orderGoods = new RpcOrderGoods();
        Order order = new Order();
        GoodsInfo goodsInfo = new GoodsInfo();

        goodsInfo.setId(goodsId);
        goodsInfo.setNumber(number);

        order.setId(orderId);
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setStatus(status.getName());

        orderGoods.setGoods(Arrays.asList(goodsInfo));
        orderGoods.setOrder(order);
        return orderGoods;
    }
}
