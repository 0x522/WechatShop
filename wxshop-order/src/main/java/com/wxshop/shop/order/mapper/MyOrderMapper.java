package com.wxshop.shop.order.mapper;

import com.wxshop.shop.api.data.GoodsInfo;
import com.wxshop.shop.api.data.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface MyOrderMapper {
    void insertOrders(OrderInfo orderInfo);

    List<GoodsInfo> getGoodsInfoOfOrder(long orderId);

//    void updateById(Order order);
}
