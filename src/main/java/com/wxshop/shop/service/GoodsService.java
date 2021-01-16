package com.wxshop.shop.service;

import com.wxshop.shop.dao.GoodsDao;
import com.wxshop.shop.generate.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {
    private GoodsDao goodsDao;

    @Autowired
    public GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public Goods createGoods(Goods goods) {
        return goodsDao.insertGoods(goods);
    }
}
