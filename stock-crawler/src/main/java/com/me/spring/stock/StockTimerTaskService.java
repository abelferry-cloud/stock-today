package com.me.spring.stock;


/**
 * 定义采集股票数据的定时任务的服务接口
 */
public interface StockTimerTaskService {
    /**
     * 获取国内大盘的实时数据信息
     */
    void getInnerMarketInfo();

    /**
     * 批量获取股票分时数据详情信息
     */
    void getStockRTIndex();

    /**
     * 获取板块数据
     */
    void getStockSectorRtIndex();

    /**
     * 获取国外大盘的实时数据信息
     */
    void getOuterMarketInfo();

}
