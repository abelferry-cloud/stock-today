package com.me.spring.stockanalysisai.service.impl;

import com.me.stock.mapper.StockRtInfoMapper;
import com.me.stock.pojo.domain.Stock4EvrDayDomain;
import com.me.stock.pojo.domain.Stock4MinuteDomain;
import com.me.stock.pojo.domain.StockUpdownDomain;
import com.me.stock.pojo.domain.StockWeeklyInfoDomain;
import com.me.spring.stockanalysisai.service.StockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票数据查询服务实现类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "stockData", keyGenerator = "simpleKeyGenerator")
public class StockDataServiceImpl implements StockDataService {

    private final StockRtInfoMapper stockRtInfoMapper;

    @Override
    @Cacheable(key = "'dayKLine:' + #code + ':' + #startDate + ':' + #endDate", unless = "#result == null || #result.isEmpty()")
    public List<Stock4EvrDayDomain> getDayKLine(String code, LocalDate startDate, LocalDate endDate) {
        log.info("查询日K线数据: code={}, startDate={}, endDate={}", code, startDate, endDate);
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return stockRtInfoMapper.getDayKLineData01(code, start, end);
    }

    @Override
    @Cacheable(key = "'weekKLine:' + #code + ':' + #date", unless = "#result == null")
    public StockWeeklyInfoDomain getWeekKLine(String code, LocalDate date) {
        log.info("查询周K线数据: code={}, date={}", code, date);
        Date curDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return stockRtInfoMapper.getStockWeeklyData(code, curDate);
    }

    @Override
    @Cacheable(key = "'minuteKLine:' + #code + ':' + #startTime + ':' + #endTime", unless = "#result == null || #result.isEmpty()")
    public List<Stock4MinuteDomain> getMinuteKLine(String code, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("查询分时数据: code={}, startTime={}, endTime={}", code, startTime, endTime);
        Date start = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
        return stockRtInfoMapper.getStockInfoByCodeAndDate(code, start, end);
    }

    @Override
    @Cacheable(key = "'increaseRanking:' + #point", unless = "#result == null || #result.isEmpty()")
    public List<StockUpdownDomain> getIncreaseRanking(LocalDateTime point) {
        log.info("查询涨幅排行: point={}", point);
        Date timePoint = Date.from(point.atZone(ZoneId.systemDefault()).toInstant());
        return stockRtInfoMapper.getNewestStockInfo(timePoint);
    }

    @Override
    @Cacheable(key = "'searchStock:' + #keyword", unless = "#result == null || #result.isEmpty()")
    public List<Map> searchStock(String keyword) {
        log.info("股票搜索: keyword={}", keyword);
        return stockRtInfoMapper.searchStock(keyword);
    }
}