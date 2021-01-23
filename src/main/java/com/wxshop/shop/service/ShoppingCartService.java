package com.wxshop.shop.service;

import com.alibaba.fastjson.JSON;
import com.wxshop.shop.controller.ShoppingCartController;
import com.wxshop.shop.dao.ShoppingCartQueryMapper;
import com.wxshop.shop.entity.*;
import com.wxshop.shop.generate.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class ShoppingCartService {
    private ShoppingCartQueryMapper shoppingCartQueryMapper;
    private GoodsMapper goodsMapper;
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper, ShoppingCartMapper shoppingCartMapper, GoodsMapper goodsMapper, SqlSessionFactory sqlSessionFactory) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
        this.goodsMapper = goodsMapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }


    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long userId, int pageNum, int pageSize) {
        //需要知道总共有多少条结果
        //需要按照结果进行分页查询
        int offset = (pageNum - 1) * pageSize;
        int totalNum = shoppingCartQueryMapper.countHowManyShopsInUserShoppingCart(userId);
        List<ShoppingCartData> pagedData = shoppingCartQueryMapper
                .selectShoppingCartDataByUserId(userId, pageSize, offset);
        int totalPage = totalNum % pageSize == 0 ?
                totalNum / pageSize : totalNum / pageSize + 1;
        Map<Long, List<ShoppingCartData>> groupByShopId = pagedData
                .stream()
                .collect(Collectors.groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()));
        List<ShoppingCartData> result = groupByShopId.values().stream().map(this::merge).collect(Collectors.toList());
        return PageResponse.pagedData(pageNum, pageSize, totalPage, result);

    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<ShoppingCartGoods> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        result.setGoods(goods);
        return result;
    }

    public ShoppingCartData addToShoppingCart(ShoppingCartController.AddToShoppingCartRequest request) {
        List<Long> goodsId = request.getGoods()
                .stream()
                .map(ShoppingCartController.AddToShoppingCartItem::getId)
                .collect(Collectors.toList());
        if (goodsId.isEmpty()) {
            throw HttpException.badRequest("商品ID为空!");
        }
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andIdIn(goodsId);
        List<Goods> goods = goodsMapper.selectByExample(goodsExample);

        if (goods.stream().map(Goods::getShopId).collect(toSet()).size() != 1) {
            log.debug("非法请求:{},{}", JSON.toJSON(goodsId), JSON.toJSON(goods));
            throw HttpException.badRequest("商品ID非法!");
        }

        Map<Long, Goods> idToGoodsMap = goods.stream().collect(toMap(Goods::getId, x -> x));
        List<ShoppingCart> shoppingCartRows = request.getGoods()
                .stream()
                .map(item -> toShoppingCartRow(item, idToGoodsMap))
                .filter(Objects::nonNull)
                .collect(toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(mapper::insert);
            sqlSession.commit();
        }
        return merge(shoppingCartQueryMapper.selectShoppingCartDataByUserIdShopId(
                UserContext.getCurrentUser().getId(),
                goods.get(0).getShopId()));
    }

    private ShoppingCart toShoppingCartRow(ShoppingCartController.AddToShoppingCartItem item,
                                           Map<Long, Goods> idToGoodsMap) {
        Goods goods = idToGoodsMap.get(item.getId());
        if (goods == null) {
            return null;
        }
        ShoppingCart result = new ShoppingCart();
        result.setGoodsId(item.getId());
        result.setNumber(item.getNumber());
        result.setShopId(goods.getShopId());
        result.setStatus(DataStatus.OK.getName());
        result.setUserId(UserContext.getCurrentUser().getId());
        result.setCreatedAt(new Date());
        result.setUpdatedAt(new Date());
        return result;
    }
}
