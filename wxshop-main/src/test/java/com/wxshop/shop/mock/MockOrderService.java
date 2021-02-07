package com.wxshop.shop.mock;

import com.wxshop.shop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderService implements OrderService {

    @Override
    public String sayHello(String name) {
        return "";
    }
}
