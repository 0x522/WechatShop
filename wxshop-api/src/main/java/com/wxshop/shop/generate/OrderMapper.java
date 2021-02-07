package com.wxshop.shop.generate;

import com.wxshop.shop.generate.Order;
import com.wxshop.shop.generate.OrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    long countByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int deleteByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int insertSelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    List<Order> selectByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    Order selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sun Feb 07 18:25:15 CST 2021
     */
    int updateByPrimaryKey(Order record);
}