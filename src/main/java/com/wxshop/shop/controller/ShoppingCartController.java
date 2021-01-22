package com.wxshop.shop.controller;

import com.wxshop.shop.entity.PageResponse;
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
    public void addToShoppingCart(@RequestBody AddToShoppingCartRequest request) {

    }

    @GetMapping("/shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(@RequestParam("pageNum") int pageNum,
                                                          @RequestParam("pageSize") int pageSize) {
        return shoppingCartService.getShoppingCartOfUser(UserContext.getCurrentUser().getId(), pageNum, pageSize);
    }

    public static class AddToShoppingCartRequest {
        List<AddToShoppingCartItem> goods;

    }

    public static class AddToShoppingCartItem {
        long id;
        int number;

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
