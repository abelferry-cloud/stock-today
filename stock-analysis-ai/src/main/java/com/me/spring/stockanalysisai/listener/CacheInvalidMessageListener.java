package com.me.spring.stockanalysisai.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * 缓存失效消息监听器
 * 监听 RabbitMQ 队列，解析消息并失效对应缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "canal.server.host")
public class CacheInvalidMessageListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    /**
     * 监听缓存失效队列
     */
    @RabbitListener(queues = "${stock.cache.invalid.queue:stock.cache.invalid.queue}")
    public void handleCacheInvalidMessage(String message) {
        try {
            log.info("Received cache invalid message: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            String tableName = jsonNode.has("tableName") ? jsonNode.get("tableName").asText() : null;
            String operationType = jsonNode.has("operationType") ? jsonNode.get("operationType").asText() : null;
            String stockCode = jsonNode.has("stockCode") ? jsonNode.get("stockCode").asText() : null;

            if (stockCode != null) {
                evictCache(tableName, operationType, stockCode);
            }

        } catch (Exception e) {
            log.error("Error processing cache invalid message: {}", message, e);
        }
    }

    /**
     * 根据消息内容失效缓存
     */
    private void evictCache(String tableName, String operationType, String stockCode) {
        // 根据不同的表名和操作类型，采用不同的缓存失效策略
        if ("stock_rt_info".equals(tableName)) {
            evictStockRtInfoCache(stockCode);
        }
        // 可扩展其他表的缓存失效逻辑
    }

    /**
     * 失效 stock_rt_info 相关的缓存
     * 日K线、周K线、分时数据、涨幅排行、搜索结果缓存
     */
    private void evictStockRtInfoCache(String stockCode) {
        log.info("Evicting cache for stockCode: {}", stockCode);

        // 获取 stockData 缓存管理器
        var stockDataCache = cacheManager.getCache("stockData");
        if (stockDataCache == null) {
            log.warn("stockData cache not found");
            return;
        }

        // 失效该股票相关的所有缓存
        // 由于 Caffeine 不支持通配符失效，我们需要清除所有相关 key
        // 这里采用清除所有缓存的策略（对于股票数据场景可接受）
        stockDataCache.clear();

        log.info("Cache evicted successfully for stockCode: {}", stockCode);
    }
}