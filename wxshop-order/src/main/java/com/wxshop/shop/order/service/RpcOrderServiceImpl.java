package com.wxshop.shop.order.service;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.GoodsInfo;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.api.data.RpcOrderGoods;
import com.wxshop.shop.api.exceptions.HttpException;
import com.wxshop.shop.order.generate.*;
import com.wxshop.shop.api.rpc.OrderRpcService;
import com.wxshop.shop.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static java.util.stream.Collectors.*;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderRpcService {
    private OrderMapper orderMapper;
    private OrderGoodsMapper orderGoodsMapper;
    private MyOrderMapper myOrderMapper;

    @Autowired
    public RpcOrderServiceImpl(OrderMapper orderMapper, OrderGoodsMapper orderGoodsMapper, MyOrderMapper myOrderMapper) {
        this.orderMapper = orderMapper;
        this.orderGoodsMapper = orderGoodsMapper;
        this.myOrderMapper = myOrderMapper;
    }

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        setOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertOrders(orderInfo);
        return order;
    }

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw HttpException.notFound("订单未找到!" + orderId);
        }
        if (order.getUserId() != userId) {
            throw HttpException.forbidden("无权访问!");
        }
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);

        order.setStatus(DataStatus.DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKey(order);

        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }


    @Override
    public PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        int count = (int) calculateCount(status);

        int totalPage = count % pageSize == 0 ?
                count / pageSize : count / pageSize + 1;

        List<Order> orders = selectOrdersWithUserId(userId, pageNum, pageSize, status);

        List<OrderGoods> orderGoods = selectOrderGoodsFromOrders(orders);

        //对订单id分组
        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoods
                .stream()
                .collect(groupingBy(OrderGoods::getOrderId, toList()));

        List<RpcOrderGoods> rpcOrderGoods = orders.stream()
                .map(order -> toRpcOrderGoods(order, orderIdToGoodsMap))
                .collect(toList());

        return PageResponse.pagedData(pageNum, pageSize, totalPage, rpcOrderGoods);
    }

    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return null;
        }
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);
        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        orderMapper.updateByPrimaryKey(order);
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(order.getId());
        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(orderMapper.selectByPrimaryKey(order.getId()));
        return result;
    }

    private RpcOrderGoods toRpcOrderGoods(Order order, Map<Long, List<OrderGoods>> orderIdToGoodsMap) {
        RpcOrderGoods result = new RpcOrderGoods();
        result.setOrder(order);
        List<GoodsInfo> goodsInfos = orderIdToGoodsMap.getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(this::toGoodsInfo)
                .collect(toList());
        result.setGoods(goodsInfos);
        return result;
    }

    private GoodsInfo toGoodsInfo(OrderGoods orderGoods) {
        GoodsInfo result = new GoodsInfo();
        result.setId(orderGoods.getGoodsId());
        result.setNumber(orderGoods.getNumber().intValue());
        return result;
    }

    private List<OrderGoods> selectOrderGoodsFromOrders(List<Order> orders) {
        List<Long> orderIds = orders.stream().map(Order::getId).collect(toList());
        OrderGoodsExample selectByOrderIds = new OrderGoodsExample();
        selectByOrderIds.createCriteria().andOrderIdIn(orderIds);
        return orderGoodsMapper.selectByExample(selectByOrderIds);
    }

    private List<Order> selectOrdersWithUserId(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        OrderExample pagedOrder = new OrderExample();
        pagedOrder.setOffset((pageNum - 1) * pageSize);
        pagedOrder.setLimit(pageSize);
        setStatus(pagedOrder, status).andUserIdEqualTo(userId);
        return orderMapper.selectByExample(pagedOrder);
    }

    private long calculateCount(DataStatus status) {
        OrderExample countByStatus = new OrderExample();
        setStatus(countByStatus, status);
        return orderMapper.countByExample(countByStatus);
    }

    private OrderExample.Criteria setStatus(OrderExample orderExample, DataStatus status) {
        if (status == null) {
            return orderExample.createCriteria().andStatusNotEqualTo(DataStatus.DELETED.getName());
        } else {
            return orderExample.createCriteria().andStatusEqualTo(status.getName());
        }
    }

    private void setOrder(Order order) {
        order.setStatus(DataStatus.PENDING.getName());
        verify(() -> order.getAddress() == null, "address 不能为空");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "total price 非法");
        verify(() -> order.getUserId() == null, "user id 不能为空");
        order.setExpressId(null);
        order.setExpressCompany(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        orderMapper.insert(order);
    }

    private void verify(BooleanSupplier booleanSupplier, String message) {
        if (booleanSupplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
