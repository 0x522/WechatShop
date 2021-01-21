package com.wxshop.shop.controller;

import com.wxshop.shop.entity.HttpException;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.generate.Shop;
import com.wxshop.shop.service.ShopService;
import com.wxshop.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ShopController {
    private ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/shop")
    public PageResponse<Shop> getShop(@RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize) {
        return shopService.getShopByUserId(UserContext.getCurrentUser().getId(), pageNum, pageSize);
    }

    @PostMapping("/shop")
    public Response<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response) {
        response.setStatus(HttpStatus.CREATED.value());
        return Response.of(shopService.createShop(shop, UserContext.getCurrentUser().getId()));
    }

    @PatchMapping("/shop/{id}")
    public Response<Shop> updateShop(@PathVariable("id") Long shopId,
                                     @RequestBody Shop shop,
                                     HttpServletResponse response) {
        shop.setId(shopId);
        try {
            Shop updatedShop = shopService.updateShop(shop, UserContext.getCurrentUser().getId());
            return Response.of(updatedShop);
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(null, e.getMessage());
        }
    }

    @DeleteMapping("/shop/{id}")
    public Response<Shop> deleteShop(@PathVariable("id") Long shopId,
                                     HttpServletResponse response) {
        try {
            return Response.of(shopService.deleteShop(shopId, UserContext.getCurrentUser().getId()));
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(null, e.getMessage());
        }
    }
}