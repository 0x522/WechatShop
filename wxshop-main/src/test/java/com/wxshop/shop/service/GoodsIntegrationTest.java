package com.wxshop.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.generate.Goods;
import com.wxshop.shop.generate.Shop;
import com.wxshop.shop.generate.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGoods() throws JsonProcessingException {
        //cookie
        String cookie = loginAndGetCookie().cookie;
        //current user
        User user = loginAndGetCookie().user;

        Shop shop = new Shop();
        shop.setName("我的微信店铺");
        shop.setDescription("我的小店开张了");
        shop.setImgUrl("http://shopUrl");
        HttpResponse shopResponse = doHttpRequest("/api/v1/shop", "POST", shop, cookie);
        Response<Shop> shopInResponse = objectMapper.readValue(shopResponse.body, new TypeReference<Response<Shop>>() {
        });

        assertEquals(HttpStatus.CREATED.value(), shopResponse.code);
        assertEquals("我的微信店铺", shopInResponse.getData().getName());
        assertEquals("我的小店开张了", shopInResponse.getData().getDescription());
        assertEquals("http://shopUrl", shopInResponse.getData().getImgUrl());
        assertEquals("ok", shopInResponse.getData().getStatus());
        assertEquals(shopInResponse.getData().getOwnerUserId(), user.getId());

        Goods goods = new Goods();
        goods.setName("肥皂");
        goods.setDescription("这是一块肥皂");
        goods.setDetails("这是一块好肥皂");
        goods.setImgUrl("http://url");
        goods.setPrice(1000L);
        goods.setStock(100);
        goods.setShopId(shopInResponse.getData().getId());
        HttpResponse goodsResponse = doHttpRequest("/api/v1/goods", "POST", goods, cookie);
        Response<Goods> goodsInResponse = objectMapper.readValue(goodsResponse.body, new TypeReference<Response<Goods>>() {
        });
        assertEquals(HttpStatus.CREATED.value(), shopResponse.code);
        assertEquals("肥皂", goodsInResponse.getData().getName());
        assertEquals("这是一块肥皂", goodsInResponse.getData().getDescription());
        assertEquals("这是一块好肥皂", goodsInResponse.getData().getDetails());
        assertEquals("http://url", goodsInResponse.getData().getImgUrl());
        assertEquals(1000L, goodsInResponse.getData().getPrice());
        assertEquals(100, goodsInResponse.getData().getStock());
        assertEquals(shopInResponse.getData().getId(), goodsInResponse.getData().getShopId());
    }

    @Test
    public void returnNotFoundIfGoodsToDeleteNotExist() throws JsonProcessingException {
        String cookie = loginAndGetCookie().cookie;
        HttpResponse response = doHttpRequest(
                "/api/v1/goods/12312312",
                "DELETE",
                null,
                cookie);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.code);
    }
}
