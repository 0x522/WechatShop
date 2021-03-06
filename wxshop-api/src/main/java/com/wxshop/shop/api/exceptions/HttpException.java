package com.wxshop.shop.api.exceptions;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private int statusCode;
    private String message;

    private HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public static HttpException forbidden(String message) {
        return new HttpException(message, HttpStatus.FORBIDDEN.value());
    }

    public static HttpException notFound(String message) {
        return new HttpException(message, HttpStatus.NOT_FOUND.value());
    }

    public static HttpException badRequest(String message) {
        return new HttpException(message, HttpStatus.BAD_REQUEST.value());
    }

    public static HttpException gone(String message) {
        return new HttpException(message, HttpStatus.GONE.value());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
