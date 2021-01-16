package com.wxshop.shop.generate;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    long countByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int deleteByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int insert(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int insertSelective(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    List<ShoppingCart> selectByExample(ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    ShoppingCart selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int updateByExampleSelective(@Param("record") ShoppingCart record, @Param("example") ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int updateByExample(@Param("record") ShoppingCart record, @Param("example") ShoppingCartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int updateByPrimaryKeySelective(ShoppingCart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shopping_cart
     *
     * @mbg.generated Sat Jan 16 15:15:51 CST 2021
     */
    int updateByPrimaryKey(ShoppingCart record);
}