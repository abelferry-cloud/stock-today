package com.me.stock.mapper;

import com.me.stock.pojo.entity.StockBusiness;

import java.util.List;

/**
* @author Jovan
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(String id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 获取所有股票代码
     * @return
     */
    List<String> getALLStockCodes();

}
