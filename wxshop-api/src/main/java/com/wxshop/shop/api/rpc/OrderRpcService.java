package com.wxshop.shop.api.rpc;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.api.data.RpcOrderGoods;
import com.wxshop.shop.order.generate.Order;

public interface OrderRpcService {
    Order createOrder(OrderInfo orderInfo, Order order);

    RpcOrderGoods deleteOrder(long orderId, long userId);

    PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status);

    RpcOrderGoods getOrderById(long orderId);

    RpcOrderGoods updateOrder(Order copy);
}
