package com.wxshop.shop.service;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.GoodsInfo;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.data.RpcOrderGoods;
import com.wxshop.shop.order.generate.Order;
import com.wxshop.shop.api.rpc.OrderRpcService;
import com.wxshop.shop.dao.GoodsDeductStockMapper;
import com.wxshop.shop.entity.GoodsWithNumber;
import com.wxshop.shop.api.exceptions.HttpException;
import com.wxshop.shop.entity.OrderResponse;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.generate.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class OrderService {
    @Reference(version = "${wxshop.orderservice.version}")
    private OrderRpcService orderRpcService;

    private UserMapper userMapper;

    private GoodsDeductStockMapper goodsStockMapper;

    private GoodsService goodsService;

    private ShopMapper shopMapper;

    @Autowired
    public OrderService(UserMapper userMapper,
                        GoodsDeductStockMapper goodsStockMapper,
                        ShopMapper shopMapper,
                        GoodsService goodsService) {
        this.userMapper = userMapper;
        this.shopMapper = shopMapper;
        this.goodsService = goodsService;
        this.goodsStockMapper = goodsStockMapper;
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(orderInfo.getGoods());
        Order createdOrder = createOrderViaRpc(orderInfo, userId, idToGoodsMap);
        return generateResponse(createdOrder, idToGoodsMap, orderInfo.getGoods());
    }

    private OrderResponse generateResponse(Order createdOrder, Map<Long, Goods> idToGoodsMap, List<GoodsInfo> goodsInfo) {
        OrderResponse response = new OrderResponse(createdOrder);

        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        response.setShop(shopMapper.selectByPrimaryKey(shopId));
        response.setGoods(
                goodsInfo
                        .stream()
                        .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                        .collect(toList())
        );

        return response;
    }

    private Map<Long, Goods> getIdToGoodsMap(List<GoodsInfo> goodsInfo) {
        if (goodsInfo.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> goodsId = goodsInfo
                .stream()
                .map(GoodsInfo::getId)
                .collect(toList());
        return goodsService.getIdToGoodsMap(goodsId);
    }

    private Order createOrderViaRpc(OrderInfo orderInfo, Long userId, Map<Long, Goods> idToGoodsMap) {
        Order order = new Order();
        order.setUserId(userId);
        order.setShopId(new ArrayList<>(idToGoodsMap.values()).get(0).getShopId());
        order.setStatus(DataStatus.PENDING.getName());
        order.setCreatedAt(new Date());

        String address = orderInfo.getAddress() == null ?
                userMapper.selectByPrimaryKey(userId).getAddress() :
                orderInfo.getAddress();

        order.setAddress(address);
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));

        return orderRpcService.createOrder(orderInfo, order);
    }

    /*
     * 扣减库存
     */
    @Transactional
    public void deductStock(OrderInfo orderInfo) {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                log.error("扣减库存失败, 商品id: " + goodsInfo.getId() + "，数量：" + goodsInfo.getNumber());
                throw HttpException.gone("扣减库存失败！");
            }
        }
    }

    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber ret = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        ret.setNumber(goodsInfo.getNumber());
        return ret;
    }

    private long calculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {
        long result = 0;

        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("goods id非法：" + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number非法：" + goodsInfo.getNumber());
            }

            result = result + goods.getPrice() * goodsInfo.getNumber();
        }

        return result;
    }

    public OrderResponse deleteOrder(long orderId, long userId) {
        return toOrderResponse(orderRpcService.deleteOrder(orderId, userId));
    }

    private OrderResponse toOrderResponse(RpcOrderGoods rpcOrderGoods) {
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(rpcOrderGoods.getGoods());
        return generateResponse(rpcOrderGoods.getOrder(), idToGoodsMap, rpcOrderGoods.getGoods());
    }


    public PageResponse<OrderResponse> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        PageResponse<RpcOrderGoods> rpcOrderGoods = orderRpcService.getOrder(userId, pageNum, pageSize, status);

        List<GoodsInfo> goodIds = rpcOrderGoods
                .getData()
                .stream()
                .map(RpcOrderGoods::getGoods)
                .flatMap(List::stream)
                .collect(toList());

        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(goodIds);

        List<OrderResponse> orders = rpcOrderGoods.getData()
                .stream()
                .map(order -> generateResponse(order.getOrder(), idToGoodsMap, order.getGoods()))
                .collect(toList());


        return PageResponse.pagedData(
                rpcOrderGoods.getPageNum(),
                rpcOrderGoods.getPageSize(),
                rpcOrderGoods.getTotalPage(),
                orders
        );
    }

    public OrderResponse updateExpressInformation(Order order, long userId) {
        doGetOrderById(userId, order.getId());

        Order copy = new Order();
        copy.setId(order.getId());
        copy.setExpressId(order.getExpressId());
        copy.setExpressCompany(order.getExpressCompany());
        copy.setUpdatedAt(new Date());
        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public OrderResponse updateOrderStatus(Order order, long userId) {
        doGetOrderById(userId, order.getId());

        Order copy = new Order();
        copy.setId(order.getId());
        copy.setStatus(order.getStatus());
        copy.setUpdatedAt(new Date());
        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public RpcOrderGoods doGetOrderById(long userId, long orderId) {
        RpcOrderGoods orderInDatabase = orderRpcService.getOrderById(orderId);
        if (orderInDatabase == null) {
            throw HttpException.notFound("订单未找到: " + orderId);
        }

        Shop shop = shopMapper.selectByPrimaryKey(orderInDatabase.getOrder().getShopId());
        if (shop == null) {
            throw HttpException.notFound("店铺未找到: " + orderInDatabase.getOrder().getShopId());
        }

        if (shop.getOwnerUserId() != userId && orderInDatabase.getOrder().getUserId() != userId) {
            throw HttpException.forbidden("无权访问！");
        }
        return orderInDatabase;
    }

    public OrderResponse getOrderById(long userId, long orderId) {
        return toOrderResponse(doGetOrderById(userId, orderId));
    }
}
