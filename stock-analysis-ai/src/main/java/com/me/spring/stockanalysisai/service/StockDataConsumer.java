package com.me.spring.stockanalysisai.service;

import com.me.stock.pojo.dto.StockDataMessage;
import com.me.spring.stockanalysisai.service.impl.StockDataConsumerImpl;
import org.springframework.stereotype.Service;

/**
 * 股票数据消费者接口
 * 用于消费来自 stock-crawler 的股票数据消息
 */
public interface StockDataConsumer {

    /**
     * 消费股票数据消息
     * 将消息内容转换为 RAG 知识库文档格式并存储
     *
     * @param message 股票数据消息
     * @return 是否处理成功
     */
    boolean consumeStockData(StockDataMessage message);
}
