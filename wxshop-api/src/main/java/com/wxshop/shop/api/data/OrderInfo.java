package com.wxshop.shop.api.data;

import java.io.Serializable;
import java.util.List;

public class OrderInfo implements Serializable {
    private List<GoodsInfo> goods;

    public OrderInfo() {

    }

    public OrderInfo(List<GoodsInfo> goods) {
        this.goods = goods;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}


