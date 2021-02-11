package com.wxshop.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.api.DataStatus;
import com.wxshop.shop.controller.ShoppingCartController;
import com.wxshop.shop.entity.*;
import com.wxshop.shop.generate.Goods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

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
                        .map(GoodsWithNumber::getPrice).collect(toList()));
        assertEquals(Arrays.asList(200, 300),
                response.getData().get(0).getGoods().stream()
                        .map(GoodsWithNumber::getNumber).collect(toList()));
    }

    @Test
    public void canAddShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetCookie();

        ShoppingCartController.AddToShoppingCartRequest request = new ShoppingCartController.AddToShoppingCartRequest();
        ShoppingCartController.AddToShoppingCartItem item = new ShoppingCartController.AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(2);
        request.setGoods(Collections.singletonList(item));

        Response<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart",
                "POST",
                request,
                userLoginResponse.cookie)
                .asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
                });

        assertEquals(1L, response.getData().getShop().getId());
        assertEquals(Arrays.asList(1L, 2L),
                response.getData().getGoods().stream().map(Goods::getId).collect(Collectors.toList()));
        assertEquals(Sets.newHashSet(2, 100),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(Collectors.toSet()));
        assertTrue(response.getData().getGoods().stream().allMatch(goods -> goods.getShopId() == 1L));
    }

    @Test
    public void canDeleteShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetCookie();
        Response<ShoppingCartData> response = doHttpRequest(
                "/api/v1/shoppingCart/5",
                "DELETE",
                null,
                userLoginResponse.cookie)
                .asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
                });
        assertEquals(2L, response.getData().getShop().getId());
        assertEquals(4L, response.getData().getGoods().get(0).getId());
        assertEquals(200, response.getData().getGoods().get(0).getNumber());
        assertEquals(DataStatus.OK.getName(), response.getData().getGoods().get(0).getStatus());
    }
}
