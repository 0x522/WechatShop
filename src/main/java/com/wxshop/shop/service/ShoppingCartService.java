package com.wxshop.shop.service;

import com.wxshop.shop.dao.ShoppingCartQueryMapper;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.entity.ShoppingCartData;
import com.wxshop.shop.entity.ShoppingCartGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ShoppingCartService {
    private ShoppingCartQueryMapper shoppingCartQueryMapper;

    @Autowired
    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
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
}
