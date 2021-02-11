package com.wxshop.shop.order.mapper;

import com.wxshop.shop.api.data.OrderInfo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface MyOrderMapper {
    void insertOrders(OrderInfo orderInfo);
}
