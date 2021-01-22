package com.wxshop.shop.generate;

import com.wxshop.shop.generate.ShoppingCart;
import com.wxshop.shop.generate.ShoppingCartExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShoppingCartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    long countByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int deleteByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int insert(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int insertSelective(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    List<ShoppingCart> selectByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    ShoppingCart selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int updateByExampleSelective(@Param("record") ShoppingCart record, @Param("example") ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int updateByExample(@Param("record") ShoppingCart record, @Param("example") ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int updateByPrimaryKeySelective(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SHOPPING_CART
     *
     * @mbg.generated Fri Jan 22 20:50:01 CST 2021
     */
    int updateByPrimaryKey(ShoppingCart record);
}