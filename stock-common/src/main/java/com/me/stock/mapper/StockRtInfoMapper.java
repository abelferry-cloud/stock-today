package com.me.stock.mapper;

import com.me.stock.pojo.domain.*;
import com.me.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 周俊远
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    List<Map> searchStock(@Param("searchStr") String searchStr);

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    List<StockUpdownDomain> getNewestStockInfo(@Param("timePoint") Date curDate);

    /**
     * 获取涨跌停数量
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param flag 1:涨停 0:跌停
     * @return List<Map>
     */
    List<Map> getStockUpdownCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("flag") int flag);

    /**
     * 获取指定股票分时数据
     * @param code 股票编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<Stock4MinuteDomain>
     */
    List<Stock4MinuteDomain> getStockInfoByCodeAndDate(@Param("code") String code,@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    /**
     * 获取指定股票日K数据
     * @param stockCode 股票编码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Stock4EvrDayDomain>
     */
    List<Stock4EvrDayDomain> getDayKLineData01(@Param("stockCode") String stockCode,@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    /**
     * 获取指定股票每日的最后交易时间
     * @param stockCode 股票编码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Date> 每日最后交易时间列表
     */
    List<Date> selectDailyMaxTime(@Param("stockCode") String stockCode,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);

    /**
     * 根据时间列表获取日K数据
     * @param stockCode 股票编码
     * @param timeList 时间列表
     * @return List<Stock4EvrDayDomain>
     */
    List<Stock4EvrDayDomain> getDayKLineDataByTimeList(@Param("stockCode") String stockCode,
                                                       @Param("timeList") List<Date> timeList);

    /**
     * 批量插入个股数据
     * @param list 数据列表
     * @return int
     */
    int insertBatch(@Param("list") List list);

    /**
     * 获取个股主要业务数据
     * @param stockCode 股票编码
     * @return StockRTMainBusinessDomain
     */
    StockRTMainBusinessDomain getStockRTMainBusinessDomain(@Param("stockCode") String stockCode);

    /**
     * 获取个股周K数据
     * @param code 股票编码
     * @param lastDate 上次查询时间
     * @return StockWeeklyInfoDomain
     */
    StockWeeklyInfoDomain getStockWeeklyData(@Param("stockCode") String code,@Param("curTime") Date lastDate);

    /**
     * 获取个股最新分时行情数据
     * @param code 股票编码
     * @param lastDate 上次查询时间
     * @return Stock4MinuteDomain
     */
    Stock4MinuteDomain getScreenDetail(@Param("code") String code,@Param("timePoint") Date lastDate);

    /**
     * 获取个股最新分时行情数据
     * @param code 股票编码
     * @return Stock4MinuteDomain
     */
    List<Stock4MinuteDomain> getScreenSecond(String code);
}
