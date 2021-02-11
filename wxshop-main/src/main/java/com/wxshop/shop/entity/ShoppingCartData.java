package com.wxshop.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wxshop.shop.generate.Shop;

import java.util.List;

public class ShoppingCartData {
    @JsonProperty("shop")
    private Shop shop;
    @JsonProperty("goods")
    private List<GoodsWithNumber> goods;

    public ShoppingCartData() {
    }

    public ShoppingCartData(Shop shop, List<GoodsWithNumber> goods) {
        this.shop = shop;
        this.goods = goods;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<GoodsWithNumber> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
