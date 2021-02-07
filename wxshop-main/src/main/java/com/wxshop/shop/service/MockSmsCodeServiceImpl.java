package com.wxshop.shop.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeServiceImpl implements SmsCodeService {
    @Override
    public String sendSmsCode(String tel) {
        return "000000";
    }
}
