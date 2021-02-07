package com.wxshop.shop.order.service;

import com.wxshop.shop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderService {
    @Override
    public String sayHello(String name) {
        return "hello," + name;
    }
}
