package com.wxshop.shop.entity;

public class Response<T> {
    T data;

    public static <T> Response<T> of(T data) {
        return new Response<T>(data);
    }

    public T getData() {
        return data;
    }

    private Response(T data) {
        this.data = data;
    }
}
