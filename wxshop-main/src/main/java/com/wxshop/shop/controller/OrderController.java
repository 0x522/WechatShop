package com.wxshop.shop.controller;

import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.api.data.OrderInfo;
import com.wxshop.shop.api.exceptions.HttpException;
import com.wxshop.shop.order.generate.Order;
import com.wxshop.shop.entity.OrderResponse;
import com.wxshop.shop.api.data.PageResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.service.OrderService;
import com.wxshop.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        orderService.deductStock(orderInfo);
        return Response.of(orderService.createOrder(orderInfo, UserContext.getCurrentUser().getId()));
    }

    @DeleteMapping("/order/{id}")
    public Response<OrderResponse> deleteOrder(@PathVariable("id") long orderId) {
        return Response.of(orderService.deleteOrder(orderId, UserContext.getCurrentUser().getId()));
    }

    @GetMapping("/order")
    public PageResponse<OrderResponse> getOrder(@RequestParam("pageNum") Integer pageNum,
                                                @RequestParam("pageSize") Integer pageSize,
                                                @RequestParam(value = "status", required = false) String status) {
        if (status != null && DataStatus.fromStatus(status) == null) {
            throw HttpException.badRequest("非法status:" + status);
        }
        return orderService.getOrder(UserContext.getCurrentUser().getId(), pageNum, pageSize, DataStatus.fromStatus(status));
    }

    @GetMapping("/order/{id}")
    public Response<OrderResponse> getOrderById(@PathVariable("id") long orderId) {
        return Response.of(orderService.getOrderById(UserContext.getCurrentUser().getId(), orderId));
    }

    @RequestMapping(value = "/order/{id}", method = {RequestMethod.POST, RequestMethod.PATCH})
    public Response<OrderResponse> updateOrder(@PathVariable("id") Integer id,
                                               @RequestBody Order order) {
        if (order.getExpressCompany() != null) {
            return Response.of(orderService.updateExpressInformation(order, UserContext.getCurrentUser().getId()));
        } else {
            return Response.of(orderService.updateOrderStatus(order, UserContext.getCurrentUser().getId()));
        }
    }
}
