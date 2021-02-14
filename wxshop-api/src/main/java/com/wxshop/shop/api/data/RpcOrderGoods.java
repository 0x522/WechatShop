package com.wxshop.shop.api.data;


import com.wxshop.shop.api.generate.Order;

import java.io.Serializable;
import java.util.List;

public class RpcOrderGoods implements Serializable {
    private Order order;
    private List<GoodsInfo> goods;

    public RpcOrderGoods() {

    }

    public RpcOrderGoods(Order order, List<GoodsInfo> goods) {
        this.order = order;
        this.goods = goods;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
