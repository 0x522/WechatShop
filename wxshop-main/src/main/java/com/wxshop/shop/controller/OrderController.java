package com.wxshop.shop.controller;

import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.entity.HttpException;
import com.wxshop.shop.entity.OrderResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.service.OrderService;
import com.wxshop.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public Response<OrderResponse> createOrder(@RequestBody OrderInfo orderInfo, HttpServletResponse response) {
        try {
            orderService.deductStock(orderInfo);
            return Response.of(orderService.createOrder(orderInfo, UserContext.getCurrentUser().getId()));
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(e.getMessage(), null);
        }
    }
}
