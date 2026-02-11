package com.stock.platform.service;

import com.me.stock.pojo.entity.StockRtInfo;
import java.util.Date;

/**
 * 股票数据查询服务
 * 用于 Function Calling 工具调用
 */
public interface StockDataService {

    /**
     * 根据股票代码和时间查询实时股票数据
     *
     * @param stockCode 股票代码（6位数字，如：000001）
     * @param currentTime 查询时间点（可以为null，查询最新数据）
     * @return 股票实时信息
     */
    StockRtInfo queryStockByCodeAndTime(String stockCode, Date currentTime);

    /**
     * 根据股票代码查询最新股票数据
     *
     * @param stockCode 股票代码（6位数字，如：000001）
     * @return 股票实时信息
     */
    StockRtInfo queryLatestStockByCode(String stockCode);
}
