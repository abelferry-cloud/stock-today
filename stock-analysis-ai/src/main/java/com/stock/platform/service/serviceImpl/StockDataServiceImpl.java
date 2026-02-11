package com.stock.platform.service.serviceImpl;

import com.me.stock.mapper.StockRtInfoMapper;
import com.me.stock.pojo.entity.StockRtInfo;
import com.stock.platform.service.StockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 股票数据查询服务实现
 * 用于 Function Calling 工具调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockDataServiceImpl implements StockDataService {

    private final StockRtInfoMapper stockRtInfoMapper;

    @Override
    public StockRtInfo queryStockByCodeAndTime(String stockCode, Date currentTime) {
        log.info("查询股票数据: stockCode={}, currentTime={}", stockCode, currentTime);

        try {
            StockRtInfo result;

            if (currentTime != null) {
                // 查询指定时间点之前的数据
                result = stockRtInfoMapper.selectByCodeAndTime(stockCode, currentTime);
            } else {
                // 查询最新数据
                result = stockRtInfoMapper.selectByStockCodeDesc(stockCode);
            }

            if (result != null) {
                log.info("查询成功: stockCode={}, stockName={}, curPrice={}",
                        result.getStockCode(), result.getStockName(), result.getCurPrice());
            } else {
                log.warn("未查询到数据: stockCode={}", stockCode);
            }

            return result;
        } catch (Exception e) {
            log.error("查询股票数据失败: stockCode={}, currentTime={}", stockCode, currentTime, e);
            return null;
        }
    }

    @Override
    public StockRtInfo queryLatestStockByCode(String stockCode) {
        log.info("查询最新股票数据: stockCode={}", stockCode);

        try {
            StockRtInfo result = stockRtInfoMapper.selectByStockCodeDesc(stockCode);

            if (result != null) {
                log.info("查询成功: stockCode={}, stockName={}, curPrice={}",
                        result.getStockCode(), result.getStockName(), result.getCurPrice());
            } else {
                log.warn("未查询到数据: stockCode={}", stockCode);
            }

            return result;
        } catch (Exception e) {
            log.error("查询最新股票数据失败: stockCode={}", stockCode, e);
            return null;
        }
    }
}
