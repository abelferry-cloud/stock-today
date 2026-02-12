package com.me.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 统一配置属性类
 * 集中管理所有队列、交换机和路由键配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq.stock")
public class RabbitMQProperties {

    // ==================== 队列名配置 ====================

    /**
     * 股票向量数据队列
     * 用于 RAG 知识库向量数据处理
     */
    private String vectorQueue = "stock.vector.queue";

    /**
     * 股票原始数据队列
     * 用于原始数据持久化（备用）
     */
    private String rawDataQueue = "stock.raw.data.queue";

    /**
     * 国内大盘信息队列
     * 用于国内大盘缓存刷新
     */
    private String innerMarketQueue = "innerMarketQueue";

    /**
     * 国外大盘信息队列
     * 用于国外大盘缓存刷新
     */
    private String outerMarketQueue = "stock.outer.market.queue";

    // ==================== 交换机配置 ====================

    /**
     * 股票数据主交换机（Topic 类型）
     * 用于路由所有股票相关数据
     */
    private String dataExchange = "stock.data.exchange";

    /**
     * 股票大盘交换机（Topic 类型）
     * 旧版大盘交换机，保留兼容
     */
    private String stockExchange = "stockExchange";

    // ==================== 路由键配置 ====================

    /**
     * 向量数据路由键
     * 用于路由到 stock.vector.queue
     */
    private String vectorRoutingKey = "stock.data.vector";

    /**
     * 原始数据路由键
     * 用于路由到 stock.raw.data.queue
     */
    private String rawDataRoutingKey = "stock.data.raw";

    /**
     * 国内大盘路由键
     * 用于路由到 innerMarketQueue
     */
    private String innerMarketRoutingKey = "inner.market";

    /**
     * 国外大盘路由键
     * 用于路由到 stock.outer.market.queue
     */
    private String outerMarketRoutingKey = "stock.outer.market";

    /**
     * 获取队列配置信息字符串（用于日志输出）
     */
    public String getConfigInfo() {
        return String.format(
            "RabbitMQ配置 - 向量队列: %s, 原始数据队列: %s, 内盘队列: %s, 外盘队列: %s, " +
            "数据交换机: %s, 股票交换机: %s",
            vectorQueue, rawDataQueue, innerMarketQueue, outerMarketQueue,
            dataExchange, stockExchange
        );
    }
}
