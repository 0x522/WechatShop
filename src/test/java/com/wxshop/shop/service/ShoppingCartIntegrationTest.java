package com.wxshop.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.entity.ShoppingCartData;
import com.wxshop.shop.entity.ShoppingCartGoods;
import com.wxshop.shop.generate.Goods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void canQueryShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetCookie();
        PageResponse<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart?pageNum=2&pageSize=1",
                "GET",
                null,
                userLoginResponse.cookie)
                .asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
                });
        assertEquals(2, response.getPageNum());
        assertEquals(1, response.getPageSize());
        assertEquals(2, response.getTotalPage());
        assertEquals(1, response.getData().size());
        assertEquals(2, response.getData().get(0).getShop().getId());
        assertEquals(Arrays.asList(4L, 5L),
                response.getData().get(0).getGoods().stream()
                        .map(Goods::getId).collect(toList()));
        assertEquals(Arrays.asList(100L, 200L),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getPrice).collect(toList()));
        assertEquals(Arrays.asList(200, 300),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getNumber).collect(toList()));
    }

}
