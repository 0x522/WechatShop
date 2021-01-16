package com.wxshop.shop.dao;

import com.wxshop.shop.generate.Goods;
import com.wxshop.shop.generate.GoodsMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsDao {
    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public GoodsDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Goods insertGoods(Goods goods) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            GoodsMapper goodsMapper = sqlSession.getMapper(GoodsMapper.class);
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            return goods;
        }
    }
}
