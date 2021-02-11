package com.wxshop.shop.api;


public enum DataStatus {
    OK(),
    DELETED(),
    //*****//
    //for order

    PENDING(),
    PAID(),
    DELIVERED(),
    RECEIVED();

    //*****//
    public String getName() {
        return name().toLowerCase();
    }
}
