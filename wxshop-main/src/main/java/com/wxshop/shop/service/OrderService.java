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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class OrderService {
    @Reference(version = "${wxshop.orderservice.version}")
    private OrderRpcService orderRpcService;

    private UserMapper userMapper;
    private GoodsService goodsService;
    private ShopMapper shopMapper;
    private GoodsDeductStockMapper goodsDeductStockMapper;

    @Autowired
    public OrderService(UserMapper userMapper,
                        GoodsService goodsService,
                        ShopMapper shopMapper,
                        GoodsDeductStockMapper goodsDeductStockMapper) {
        this.userMapper = userMapper;
        this.goodsService = goodsService;
        this.shopMapper = shopMapper;
        this.goodsDeductStockMapper = goodsDeductStockMapper;
    }

    public PageResponse<OrderResponse> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        PageResponse<RpcOrderGoods> rpcOrderGoods = orderRpcService.getOrder(userId, pageNum, pageSize, status);
        List<GoodsInfo> goodsInfo = rpcOrderGoods
                .getData()
                .stream()
                .map(RpcOrderGoods::getGoods)
                .flatMap(List::stream)
                .collect(toList());
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(goodsInfo);
        List<OrderResponse> orders = rpcOrderGoods
                .getData()
                .stream()
                .map(order -> generateResponse(order.getOrder(), idToGoodsMap, order.getGoods()))
                .collect(toList());
        return PageResponse.pagedData(rpcOrderGoods.getPageNum(),
                rpcOrderGoods.getPageSize(),
                rpcOrderGoods.getTotalPage(),
                orders);
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {

        //获取订单中商品 id->Goods 的映射
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(orderInfo.getGoods());

        //通过Rpc创建订单
        Order createdOrder = createOrderViaRpc(orderInfo, userId, idToGoodsMap);

        //返回响应
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
        List<Long> goodsId = goodsInfo
                .stream()
                .map(GoodsInfo::getId)
                .collect(toList());
        return goodsService.getIdToGoodsMap(goodsId);
    }

    private Order createOrderViaRpc(OrderInfo orderInfo, Long userId, Map<Long, Goods> idToGoodsMap) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(DataStatus.PENDING.getName());
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(caculateTotalPrice(orderInfo, idToGoodsMap));
        return orderRpcService.createOrder(orderInfo, order);
    }

    /*
     * 扣减库存
     */
    @Transactional
    public void deductStock(OrderInfo orderInfo) {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsDeductStockMapper.deductStock(goodsInfo) <= 0) {
                log.debug("扣减库存失败, 商品id: " + goodsInfo.getId() + "，数量：" + goodsInfo.getNumber());
                throw HttpException.gone("扣减库存失败！");
            }
        }
    }

    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber result = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        result.setNumber(goodsInfo.getNumber());
        return result;
    }

    private long caculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {
        long result = 0;
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("goods id 非法" + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number 非法" + goodsInfo.getNumber());
            }
            result = result + goods.getPrice() * goodsInfo.getNumber();
        }
        return result;
    }

    public OrderResponse deleteOrder(long orderId, long userId) {
        RpcOrderGoods rpcOrderGoods = orderRpcService.deleteOrder(orderId, userId);
        return toOrderResponse(rpcOrderGoods);
    }

    private OrderResponse toOrderResponse(RpcOrderGoods rpcOrderGoods) {
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(rpcOrderGoods.getGoods());
        return generateResponse(rpcOrderGoods.getOrder(), idToGoodsMap, rpcOrderGoods.getGoods());
    }

    public OrderResponse updateExpressInformation(Order order, long userId) {
        doGetOrderById(userId, order.getId());
        //防止前端注入有害信息
        //新建Order对象,只注入有用信息
        Order copy = new Order();
        copy.setId(order.getId());
        copy.setExpressId(order.getExpressId());
        copy.setExpressCompany(order.getExpressCompany());
        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public OrderResponse updateOrderStatus(Order order, long userId) {
        doGetOrderById(userId, order.getId());
        //防止前端注入有害信息
        //新建Order对象,只注入有用信息
        Order copy = new Order();
        copy.setId(order.getId());
        copy.setStatus(order.getStatus());
        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public OrderResponse getOrderById(Long userId, long orderId) {
        return toOrderResponse(doGetOrderById(userId, orderId));
    }

    private RpcOrderGoods doGetOrderById(Long userId, long orderId) {
        RpcOrderGoods orderInDB = orderRpcService.getOrderById(orderId);
        if (orderInDB == null) {
            throw HttpException.notFound("订单未找到: " + orderId);
        }
        Shop shop = shopMapper.selectByPrimaryKey(orderInDB.getOrder().getShopId());
        if (shop == null) {
            throw HttpException.notFound("店铺未找到: " + orderInDB.getOrder().getShopId());
        }

        if (!shop.getOwnerUserId().equals(userId) && !orderInDB.getOrder().getUserId().equals(userId)) {
            throw HttpException.forbidden("无权访问！");
        }
        return orderInDB;
    }
}
