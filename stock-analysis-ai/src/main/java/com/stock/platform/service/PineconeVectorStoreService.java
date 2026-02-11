package com.stock.platform.service;

import com.me.stock.pojo.dto.StockDataMessage;

import java.util.List;

/**
 * Pinecone向量存储服务接口
 * 负责将股票数据转换为文档并存入Pinecone向量数据库
 */
public interface PineconeVectorStoreService {

    /**
     * 将股票数据消息存入向量数据库
     *
     * @param message 股票数据消息
     */
    void storeStockData(StockDataMessage message);

    /**
     * 批量存储股票数据
     *
     * @param messages 股票数据消息列表
     */
    void batchStoreStockData(List<StockDataMessage> messages);

    /**
     * 删除指定股票的向量数据
     *
     * @param stockCode 股票代码
     */
    void deleteByStockCode(String stockCode);
}
