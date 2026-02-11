package com.stock.platform.consumer;

import com.rabbitmq.client.Channel;
import com.me.stock.config.RabbitMQProperties;
import com.me.stock.pojo.dto.StockDataMessage;
import com.stock.platform.service.PineconeVectorStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 股票数据向量存储消费者
 * 监听stock-crawler发送的股票数据，并将其存储到Pinecone向量数据库
 */
@Component
@Slf4j
public class StockDataVectorConsumer {

    @Autowired
    private PineconeVectorStoreService vectorStoreService;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 消费股票向量数据
     * 监听队列：stock.vector.queue
     *
     * @param message 股票数据消息
     * @param channel RabbitMQ通道
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = "#{rabbitMQProperties.vectorQueue}",
            containerFactory = "rabbitListenerContainerFactory")
    public void consumeVectorData(StockDataMessage message,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到股票向量数据: messageId={}, stockCode={}, dataType={}, title={}",
                    message.getMessageId(),
                    message.getStockCode(),
                    message.getDataType(),
                    message.getTitle());

            // 验证消息必填字段
            validateMessage(message);

            // 存储到向量数据库
            vectorStoreService.storeStockData(message);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);

            log.debug("成功处理股票向量数据: messageId={}", message.getMessageId());

        } catch (IllegalArgumentException e) {
            log.error("消息验证失败，拒绝消息: messageId={}, error={}",
                    message.getMessageId(), e.getMessage());
            try {
                // 消息格式错误，直接拒绝（不重新入队）
                channel.basicReject(deliveryTag, false);
            } catch (Exception ex) {
                log.error("拒绝消息失败: {}", ex.getMessage());
            }
        } catch (Exception e) {
            log.error("处理股票向量数据失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            try {
                // 处理失败，拒绝并重新入队（可根据业务需求调整）
                // 注意：如果是持久性错误（如数据格式问题），应使用basicReject不重新入队
                // 如果是临时性错误（如网络问题），可以使用basicNack重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("拒绝消息失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 验证消息必填字段
     */
    private void validateMessage(StockDataMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("消息不能为空");
        }
        if (message.getStockCode() == null || message.getStockCode().isEmpty()) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
        if (message.getDataType() == null || message.getDataType().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException("数据内容不能为空");
        }
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            throw new IllegalArgumentException("消息ID不能为空");
        }
    }

    /**
     * 消费股票原始数据（备用）
     * 监听队列：stock.raw.data.queue
     * 可用于原始数据的持久化或其他处理
     *
     * @param message 股票数据消息
     * @param channel RabbitMQ通道
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = "#{rabbitMQProperties.rawDataQueue}",
            containerFactory = "rabbitListenerContainerFactory")
    public void consumeRawData(StockDataMessage message,
                                Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到股票原始数据: messageId={}, stockCode={}, dataType={}",
                    message.getMessageId(),
                    message.getStockCode(),
                    message.getDataType());

            // 这里可以进行原始数据的额外处理
            // 例如：持久化到MySQL、缓存到Redis等

            // 手动确认消息
            channel.basicAck(deliveryTag, false);

            log.debug("成功处理股票原始数据: messageId={}", message.getMessageId());

        } catch (Exception e) {
            log.error("处理股票原始数据失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("拒绝消息失败: {}", ex.getMessage());
            }
        }
    }
}
