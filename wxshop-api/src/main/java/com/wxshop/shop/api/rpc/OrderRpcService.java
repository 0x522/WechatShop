package com.wxshop.shop.api.rpc;

import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.generate.Order;

public interface OrderRpcService {
    Order createOrder(OrderInfo orderInfo, Order order);
}
