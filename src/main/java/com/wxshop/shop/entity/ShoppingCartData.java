package com.wxshop.shop.entity;

import com.wxshop.shop.generate.Shop;

import java.util.List;

public class ShoppingCartData {
    private Shop shop;
    private List<ShoppingCartGoods> goods;

    public ShoppingCartData() {
    }

    public ShoppingCartData(Shop shop, List<ShoppingCartGoods> goods) {
        this.shop = shop;
        this.goods = goods;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<ShoppingCartGoods> getGoods() {
        return goods;
    }

    public void setGoods(List<ShoppingCartGoods> goods) {
        this.goods = goods;
    }
}
