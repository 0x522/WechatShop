package com.wxshop.shop.mock;

import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.generate.Order;
import com.wxshop.shop.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    public OrderRpcService orderRpcService;


    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return orderRpcService.createOrder(orderInfo, order);
    }
}
