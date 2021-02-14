package com.wxshop.shop.api.data;

import java.io.Serializable;
import java.util.List;

public class OrderInfo implements Serializable {
    private long orderId;
    private List<GoodsInfo> goods;

    public OrderInfo() {

    }

    public OrderInfo(long orderId, List<GoodsInfo> goods) {
        this.orderId = orderId;
        this.goods = goods;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}

