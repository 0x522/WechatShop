package com.wxshop.shop.order.service;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.GoodsInfo;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.api.data.RpcOrderGoods;
import com.wxshop.shop.api.exceptions.HttpException;
import com.wxshop.shop.order.generate.Order;
import com.wxshop.shop.order.generate.OrderGoodsMapper;
import com.wxshop.shop.order.generate.OrderMapper;
import com.wxshop.shop.order.mapper.MyOrderMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class RpcOrderServiceImplTest {

    private String databaseUrl = "jdbc:mysql://localhost:3307/order?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private String databaseUsername = "root";
    private String databasePassword = "root";
    RpcOrderServiceImpl rpcOrderService;
    SqlSession sqlSession;

    @BeforeEach
    void setUpDB() throws IOException {
        // 在每个测试开始前，执行一次flyway:clean flyway:migrate
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        String resource = "test-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession(true);

        rpcOrderService = new RpcOrderServiceImpl(
                sqlSession.getMapper(OrderMapper.class),
                sqlSession.getMapper(MyOrderMapper.class),
                sqlSession.getMapper(OrderGoodsMapper.class)
        );
    }

    @AfterEach
    void cleanUp() {
        sqlSession.close();
    }

    @Test
    void getOrderByPageTest() {
        PageResponse<RpcOrderGoods> result = rpcOrderService.getOrder(1L, 2, 1, null);
        Assertions.assertEquals(2, result.getTotalPage());
        Assertions.assertEquals(2, result.getPageNum());
        Assertions.assertEquals(1, result.getPageSize());
        Assertions.assertEquals(1, result.getData().size());

        Order order = result.getData().get(0).getOrder();
        Assertions.assertEquals(2L, order.getId());
        Assertions.assertEquals(700, order.getTotalPrice());
        Assertions.assertEquals(1L, order.getUserId());
        Assertions.assertEquals(1L, order.getShopId());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals(DataStatus.PENDING.getName(), order.getStatus());

        List<GoodsInfo> goods = result.getData().get(0).getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L), goods.stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 4), goods.stream().map(GoodsInfo::getNumber).collect(toList()));
    }

    @Test
    void createOrderTest() {

        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1, 2);
        GoodsInfo goods2 = new GoodsInfo(2, 10);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));

        Order order = new Order();
        order.setUserId(1L);
        order.setShopId(1L);
        order.setAddress("火星");
        order.setTotalPrice(10000L);

        Order orderWithId = rpcOrderService.createOrder(orderInfo, order);
        Assertions.assertNotNull(orderWithId.getId());


        RpcOrderGoods orderInDB = rpcOrderService.getOrderById(orderWithId.getId());

        Assertions.assertEquals(1L, orderInDB.getOrder().getUserId());
        Assertions.assertEquals(1L, orderInDB.getOrder().getShopId());
        Assertions.assertEquals("火星", orderInDB.getOrder().getAddress());
        Assertions.assertEquals(10000L, orderInDB.getOrder().getTotalPrice());
        Assertions.assertEquals(DataStatus.PENDING.getName(), orderInDB.getOrder().getStatus());
    }

    @Test
    void updateOrderTest() {
        Order order = rpcOrderService.getOrderById(2L).getOrder();
        order.setExpressCompany("中通");
        order.setExpressId("中通12345");
        order.setStatus(DataStatus.DELIVERED.getName());

        RpcOrderGoods result = rpcOrderService.updateOrder(order);

        Assertions.assertEquals(2L, result.getOrder().getId());
        Assertions.assertEquals(700, result.getOrder().getTotalPrice());
        Assertions.assertEquals(1L, result.getOrder().getUserId());
        Assertions.assertEquals(1L, result.getOrder().getUserId());
        Assertions.assertEquals("中通", result.getOrder().getExpressCompany());
        Assertions.assertEquals("中通12345", result.getOrder().getExpressId());
        Assertions.assertEquals("火星", result.getOrder().getAddress());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), order.getStatus());

        List<GoodsInfo> goodsInfos = result.getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goodsInfos.stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goodsInfos.stream().map(GoodsInfo::getNumber).collect(toList()));
    }

    @Test
    void deleteOrderTest() {
        RpcOrderGoods deletedOrder = rpcOrderService.deleteOrder(2L, 1L);
        Order order = deletedOrder.getOrder();
        Assertions.assertEquals(2L, order.getId());
        Assertions.assertEquals(700, order.getTotalPrice());
        Assertions.assertEquals(1L, order.getUserId());
        Assertions.assertEquals(1L, order.getShopId());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals(DataStatus.DELETED.getName(), order.getStatus());

        List<GoodsInfo> goodsInfos = deletedOrder.getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goodsInfos.stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goodsInfos.stream().map(GoodsInfo::getNumber).collect(toList()));
    }

    @Test
    public void throwExceptionIfNotAuthorized() {
        HttpException exception = Assertions.assertThrows(HttpException.class, () -> {
            rpcOrderService.deleteOrder(2L, 0L);
        });

        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode());
    }
}
