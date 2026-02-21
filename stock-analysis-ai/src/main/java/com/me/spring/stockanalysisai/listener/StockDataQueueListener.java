package com.me.spring.stockanalysisai.listener;

import com.me.stock.pojo.dto.StockDataMessage;
import com.me.spring.stockanalysisai.service.StockDataConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 股票数据队列监听器
 * 监听 RabbitMQ 队列，消费来自 stock-crawler 的股票数据消息
 * 并将数据添加到 RAG 知识库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataQueueListener {

    private final StockDataConsumer stockDataConsumer;

    /**
     * 监听股票数据向量队列
     * 接收来自 stock-crawler 的股票数据消息，转换为 RAG 文档并存储
     *
     * @param message 股票数据消息
     */
    @RabbitListener(queues = "${stock.rabbitmq.vector-queue:stock.vector.queue}")
    public void handleStockDataMessage(StockDataMessage message) {
        log.info("收到股票数据消息: messageId={}, stockCode={}, stockName={}, dataType={}",
                message.getMessageId(),
                message.getStockCode(),
                message.getStockName(),
                message.getDataType());

        try {
            boolean success = stockDataConsumer.consumeStockData(message);
            if (success) {
                log.info("股票数据消息处理成功: messageId={}", message.getMessageId());
            } else {
                log.warn("股票数据消息处理失败: messageId={}", message.getMessageId());
            }
        } catch (Exception e) {
            log.error("处理股票数据消息异常: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            throw e;
        }
    }
}