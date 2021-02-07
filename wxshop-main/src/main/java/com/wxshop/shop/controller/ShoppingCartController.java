package com.wxshop.shop.controller;

import com.wxshop.shop.entity.HttpException;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.entity.ShoppingCartData;
import com.wxshop.shop.service.ShoppingCartService;
import com.wxshop.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/shoppingCart")
    public Response<ShoppingCartData> addToShoppingCart(@RequestBody AddToShoppingCartRequest request) {
        try {
            return Response.of(shoppingCartService.addToShoppingCart(request, UserContext.getCurrentUser().getId()));
        } catch (HttpException e) {
            return Response.of(e.getMessage(), null);
        }
    }

    @DeleteMapping("/shoppingCart/{goodsId}")
    public Response<ShoppingCartData> deleteGoodsInShoppingCart(@PathVariable("goodsId") Long goodsId) {
        try {
            return Response.of(shoppingCartService.deleteGoodsInShoppingCart(goodsId, UserContext.getCurrentUser().getId()));
        } catch (HttpException e) {
            return Response.of(e.getMessage(), null);
        }
    }

    @GetMapping("/shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(@RequestParam("pageNum") int pageNum,
                                                          @RequestParam("pageSize") int pageSize) {
        return shoppingCartService.getShoppingCartOfUser(UserContext.getCurrentUser().getId(), pageNum, pageSize);
    }

    public static class AddToShoppingCartRequest {
        private List<AddToShoppingCartItem> goods;

        public List<AddToShoppingCartItem> getGoods() {
            return goods;
        }

        public void setGoods(List<AddToShoppingCartItem> goods) {
            this.goods = goods;
        }
    }

    public static class AddToShoppingCartItem {
        long id;
        int number;

        public AddToShoppingCartItem() {

        }

        public AddToShoppingCartItem(long id, int number) {
            this.id = id;
            this.number = number;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
