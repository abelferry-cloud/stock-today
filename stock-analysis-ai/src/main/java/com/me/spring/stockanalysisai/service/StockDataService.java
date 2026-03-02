package com.me.spring.stockanalysisai.service;

import com.me.stock.pojo.domain.Stock4EvrDayDomain;
import com.me.stock.pojo.domain.Stock4MinuteDomain;
import com.me.stock.pojo.domain.StockUpdownDomain;
import com.me.stock.pojo.domain.StockWeeklyInfoDomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 股票数据查询服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface StockDataService {

    /**
     * 获取日K线数据
     *
     * @param code      股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日K线数据列表
     */
    List<Stock4EvrDayDomain> getDayKLine(String code, LocalDate startDate, LocalDate endDate);

    /**
     * 获取周K线数据
     *
     * @param code 股票代码
     * @param date 查询日期（返回包含该日期的周K线数据）
     * @return 周K线数据
     */
    StockWeeklyInfoDomain getWeekKLine(String code, LocalDate date);

    /**
     * 获取分时数据
     *
     * @param code      股票代码
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 分时数据列表
     */
    List<Stock4MinuteDomain> getMinuteKLine(String code, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取涨幅排行
     *
     * @param point 时间点
     * @return 涨幅排行列表
     */
    List<StockUpdownDomain> getIncreaseRanking(LocalDateTime point);

    /**
     * 股票搜索
     *
     * @param keyword 搜索关键字（股票代码或名称）
     * @return 搜索结果列表
     */
    List<Map> searchStock(String keyword);
}