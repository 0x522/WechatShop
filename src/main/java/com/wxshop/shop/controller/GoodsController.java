package com.wxshop.shop.controller;

import com.wxshop.shop.entity.HttpException;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.entity.Response;
import com.wxshop.shop.generate.Goods;
import com.wxshop.shop.service.GoodsService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("/goods")
    public @ResponseBody
    PageResponse<Goods> getGoods(@RequestParam("pageNumber") Integer pageNumber,
                                 @RequestParam("pageSize") Integer pageSize,
                                 @RequestParam(value = "shopId", required = false) Integer shopId) {
        PageResponse<Goods> goods = goodsService.getGoods(pageNumber, pageSize, shopId);
        return goods;
    }


    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody Goods goods, HttpServletResponse response) {
        clean(goods);
        response.setStatus(HttpStatus.SC_CREATED);
        try {
            return Response.of(goodsService.createGoods(goods));
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(null, e.getMessage());
        }
    }

    @DeleteMapping("/goods/{id}")
    public Response<Goods> deleteGoods(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.SC_NO_CONTENT);
            return Response.of(goodsService.deleteGoodsById(id));
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(null, e.getMessage());
        }
    }

    @PatchMapping("/goods/{id}")
    public Response<Goods> updateGoods(@RequestBody Goods goods, HttpServletResponse response) {
        try {
            return Response.of(goodsService.updateGoods(goods));
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            return Response.of(null, e.getMessage());
        }
    }

    private void clean(Goods goods) {
        //数据清洗
        goods.setId(null);
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
    }
}
