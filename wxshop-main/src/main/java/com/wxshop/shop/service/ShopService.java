package com.wxshop.shop.service;

import com.wxshop.shop.entity.DataStatus;
import com.wxshop.shop.entity.HttpException;
import com.wxshop.shop.entity.PageResponse;
import com.wxshop.shop.generate.Shop;
import com.wxshop.shop.generate.ShopExample;
import com.wxshop.shop.generate.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShopService {
    private ShopMapper shopMapper;

    @Autowired
    public ShopService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public PageResponse<Shop> getShopByUserId(Long userId, int pageNum, int pageSize) {
        ShopExample countByStatus = new ShopExample();
        countByStatus.createCriteria().andStatusEqualTo(DataStatus.DELETED.getName());
        int totalNumber = (int) shopMapper.countByExample(countByStatus);
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

        ShopExample pageCondition = new ShopExample();
        pageCondition.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
        pageCondition.setLimit(pageSize);
        pageCondition.setOffset((pageNum - 1) * pageSize);

        List<Shop> pagedShops = shopMapper.selectByExample(pageCondition);
        return PageResponse.pagedData(pageNum, pageSize, totalPage, pagedShops);
    }

    public Shop createShop(Shop shop, Long creatorId) {
        shop.setOwnerUserId(creatorId);
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());
        shop.setStatus(DataStatus.OK.getName());
        long shopId = shopMapper.insert(shop);
        shop.setId(shopId);
        return shop;
    }

    public Shop updateShop(Shop shop, Long userId) {
        Shop shopInDB = shopMapper.selectByPrimaryKey(shop.getId());
        if (shopInDB == null) {
            throw HttpException.notFound("未找到店铺!");
        }
        if (!Objects.equals(shopInDB.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问!");
        }
        shopInDB.setUpdatedAt(new Date());
        shopMapper.updateByPrimaryKey(shop);
        return shop;
    }

    public Shop deleteShop(Long shopId, Long userId) {
        Shop shopInDB = shopMapper.selectByPrimaryKey(shopId);
        if (shopInDB == null) {
            throw HttpException.notFound("未找到店铺!");
        }
        if (!Objects.equals(shopInDB.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问!");
        }
        shopInDB.setUpdatedAt(new Date());
        shopInDB.setStatus(DataStatus.DELETED.getName());
        shopMapper.updateByPrimaryKey(shopInDB);
        return shopInDB;
    }
}

