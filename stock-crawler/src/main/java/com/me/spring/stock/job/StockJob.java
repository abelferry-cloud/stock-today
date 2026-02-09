package com.me.spring.stock.job;

import com.me.spring.stock.StockTimerTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  定义xxl-job任务执行的bean
 */
@Component
@Slf4j
public class StockJob {

    /**
     * 注入股票定时任务服务bean
     */
    @Autowired
    private StockTimerTaskService stockTimerTaskService;


    /**
     * 定义定时任务，采集国内大盘数据
     */
    @XxlJob("getInnerMarketInfo")
    public void getStockInnerMarketInfos(){
        stockTimerTaskService.getInnerMarketInfo();
    }

    /**
     * 定时任务，采集个股实时数据
     */
    @XxlJob("getStockRTIndex")
    public void getStockRTIndex(){
        stockTimerTaskService.getStockRTIndex();
    }

    /**
     * 定时任务，采集外盘实时数据
     */
    @XxlJob("getOuterMarketInfo")
    public void getOuterMarketInfo(){
        stockTimerTaskService.getOuterMarketInfo();
    }

}
