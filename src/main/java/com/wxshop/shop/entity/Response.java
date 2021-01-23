package com.wxshop.shop.entity;


public class Response<T> {
    private T data;

    private String message;

    public static <T> Response<T> of(T data, String message) {
        return new Response<T>(data, message);
    }

    public static <T> Response<T> of(T data) {
        return new Response<T>(data);
    }

    public static <T> Response<T> of(String message) {
        return new Response<T>(null, message);
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    private Response(T data, String message) {
        this.data = data;
        this.message = message;
    }

    private Response(T data) {
        this.data = data;
    }

    private Response() {
    }
}
