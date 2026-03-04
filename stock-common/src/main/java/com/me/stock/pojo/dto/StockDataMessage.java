package com.me.stock.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 股票数据消息DTO
 * 供stock-crawler模块发送数据到RAG知识库使用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDataMessage {

    /**
     * 消息唯一标识
     */
    private String messageId;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 数据类型：INNER_MARKET（国内大盘）、OUTER_MARKET（国外大盘）、
     *          STOCK_RT（个股实时）、BLOCK_SECTOR（板块实时）
     */
    private String dataType;

    /**
     * 数据标题
     */
    private String title;

    /**
     * 数据内容
     */
    private String content;

    /**
     * 数据发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 数据来源（如：新浪财经等）
     */
    private String source;

    /**
     * 数据URL
     */
    private String url;

    /**
     * 扩展字段（JSON格式）
     */
    private Map<String, Object> metadata;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 数据分类标签
     */
    private String[] tags;
}
