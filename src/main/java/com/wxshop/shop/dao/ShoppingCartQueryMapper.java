package com.wxshop.shop.dao;

import com.wxshop.shop.entity.ShoppingCartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartQueryMapper {
    int countHowManyShopsInUserShoppingCart(@Param("userId") long userId);

    List<ShoppingCartData> selectShoppingCartDataByUserId(@Param("userId") long userId,
                                                          @Param("limit") int limit,
                                                          @Param("offset") int offset);

    List<ShoppingCartData> selectShoppingCartDataByUserIdShopId(@Param("userId") Long userId,
                                                                @Param("shopId") Long shopId
    );
}
