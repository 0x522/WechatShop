package com.wxshop.shop.dao;

import com.wxshop.shop.api.data.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsDeductStockMapper {
    int deductStock(GoodsInfo goodsInfo);
}
