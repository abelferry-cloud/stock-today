package com.me.stock.mapper;

import com.me.stock.pojo.domain.InnerMarketDomain;
import com.me.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    /**
     * 根据指定时间点查询指定大盘编码的数据
     * @param curDate 指定时间点
     * @param mCodes 大盘编码合集
     * @return
     */
    List<InnerMarketDomain> getMarketInfo(@Param("curDate") Date curDate,@Param("marketCodes") List<String> mCodes);

    /**
     * 获取指定时间段内的大盘成交量数据
     * @param openDate 开始时间,一般指开盘时间
     * @param endDate 结束时间 一般指收盘时间
     * @param marketCodes 大盘编码合集
     * @return List<Map>
     */
    List<Map> getSumAmtInfo(@Param("openDate") Date openDate, @Param("endDate") Date endDate, @Param("markedIds") List<String> markedIds);

    /**
     * 获取指定时间段内，指定大盘的涨跌范围数据
     * @param curDate 指定时间点
     * @return List<Map>
     */
    List<Map> stockUpDownScopeCount(@Param("curDate") Date curDate);

    /**
     * 批量插入数据
     * @param entities 待插入的entity合集
     * @return 插入的行数
     */
    int insertBatch(@Param("entities") List<StockMarketIndexInfo> entities);
}
