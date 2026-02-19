package com.me.spring.stockanalysisai.listener;

import com.me.spring.stockanalysisai.service.StockDataConsumer;
import com.me.stock.pojo.dto.StockDataMessage;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 股票数据队列监听器
 * 监听来自 stock-crawler 的股票数据消息，并调用消费者服务处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataQueueListener {

    private final StockDataConsumer stockDataConsumer;
    private final ConnectionFactory connectionFactory;

    /**
     * 初始化时检查 RabbitMQ 连接状态
     */
    @PostConstruct
    public void init() {
        try {
            Connection connection = connectionFactory.createConnection();
            boolean isOpen = connection.isOpen();
            log.info("========== StockDataQueueListener 初始化 ==========");
            log.info("RabbitMQ 连接状态: {}", isOpen ? "已连接" : "未连接");
            log.info("RabbitMQ Host: {}", connectionFactory.getHost());
            log.info("RabbitMQ Port: {}", connectionFactory.getPort());
            log.info("RabbitMQ Virtual Host: {}", connectionFactory.getVirtualHost());
            log.info("监听队列名称: stock.vector.queue");
            log.info("StockDataConsumer 实例: {}", stockDataConsumer != null ? "已注入" : "未注入");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("StockDataQueueListener 初始化失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 监听股票向量数据队列
     * 消息处理模式：手动 ACK
     * 失败重试：最大重试 3 次
     */
    @RabbitListener(queues = "${spring.rabbitmq.stock.vectorQueue:stock.vector.queue}")
    public void onStockVectorMessage(StockDataMessage message, Channel channel, Message amqpMessage) {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到股票向量数据消息: messageId={}, dataType={}, stockCode={}, deliveryTag={}",
                    message.getMessageId(), message.getDataType(), message.getStockCode(), deliveryTag);

            // 处理消息
            boolean success = stockDataConsumer.consumeStockData(message);

            if (success) {
                // 处理成功，手动确认 ACK
                channel.basicAck(deliveryTag, false);
                log.debug("消息处理成功并确认ACK: messageId={}, deliveryTag={}",
                        message.getMessageId(), deliveryTag);
            } else {
                // 处理失败，拒绝消息（不重新入队）
                log.warn("消息处理失败，拒绝消息: messageId={}, deliveryTag={}",
                        message.getMessageId(), deliveryTag);
                channel.basicReject(deliveryTag, false);
            }

        } catch (Exception e) {
            log.error("处理股票向量数据消息异常: messageId={}, deliveryTag={}, error={}",
                    message.getMessageId(), deliveryTag, e.getMessage(), e);

            try {
                // 发生异常，拒绝消息并重新入队（最多重试次数由 RabbitMQ 配置控制）
                // 注意：如果消息一直失败，可能会导致无限重试，建议结合死信队列使用
                boolean requeue = shouldRequeue(amqpMessage);
                channel.basicReject(deliveryTag, requeue);
                log.warn("消息处理异常，{}: messageId={}, deliveryTag={}",
                        requeue ? "重新入队" : "拒绝消息",
                        message.getMessageId(), deliveryTag);

            } catch (IOException ioException) {
                log.error("拒绝消息失败: deliveryTag={}, error={}",
                        deliveryTag, ioException.getMessage(), ioException);
            }
        }
    }

    /**
     * 判断是否应该重新入队
     * 根据消息的重试次数或其他条件决定是否重新入队
     */
    private boolean shouldRequeue(Message amqpMessage) {
        // 获取重试次数（需要在消息头中设置）
        Integer retryCount = (Integer) amqpMessage.getMessageProperties()
                .getHeaders().getOrDefault("x-retry-count", 0);

        // 如果重试次数超过阈值，则不重新入队，发送到死信队列
        int maxRetryCount = 3;
        if (retryCount >= maxRetryCount) {
            log.warn("消息已达到最大重试次数 ({}), 不再重新入队，将发送到死信队列", maxRetryCount);
            return false;
        }

        // 否则重新入队
        return true;
    }
}
