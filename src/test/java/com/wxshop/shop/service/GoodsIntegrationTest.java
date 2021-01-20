package com.wxshop.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.generate.Goods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGoods() throws JsonProcessingException {
        String cookie = loginAndGetCookie().cookie;
        Goods goods = new Goods();
        goods.setName("肥皂");
        goods.setDescription("这是一块肥皂");
        goods.setDetails("这是一块好肥皂");
        goods.setImgUrl("http://url");
        goods.setPrice(1000L);
        goods.setStock(100);
        goods.setShopId(1L);
        HttpResponse response = doHttpRequest("/api/v1/goods", "POST", goods, cookie);
        Response<Goods> goodsInResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.code);
        Assertions.assertEquals("肥皂", goodsInResponse.getData().getName());
    }

    @Test
    public void testDeleteGoods() {

    }
}
