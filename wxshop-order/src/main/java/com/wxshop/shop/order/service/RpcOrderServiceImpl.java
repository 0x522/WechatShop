package com.wxshop.shop.order.service;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.generate.Order;
import com.wxshop.shop.api.generate.OrderMapper;
import com.wxshop.shop.api.rpc.OrderRpcService;
import com.wxshop.shop.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderRpcService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MyOrderMapper myOrderMapper;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        setOrder(order);
        myOrderMapper.insertOrders(orderInfo);
        return order;
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
        long id = orderMapper.insert(order);
        order.setId(id);
    }

    private void verify(BooleanSupplier booleanSupplier, String message) {
        if (booleanSupplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
