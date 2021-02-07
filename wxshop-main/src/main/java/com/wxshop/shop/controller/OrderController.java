package com.wxshop.shop.controller;

import com.wxshop.shop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    @Reference(version = "${wxshop.orderservice.version}")
    private OrderService orderService;

    @RequestMapping("/testRpc")
    public String testRpc() {
        return orderService.sayHello("zhangsan");
    }
}
