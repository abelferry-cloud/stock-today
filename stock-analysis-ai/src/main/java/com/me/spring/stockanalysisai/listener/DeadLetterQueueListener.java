package com.me.spring.stockanalysisai.listener;

import com.me.stock.pojo.dto.StockDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 死信队列监听器
 * 监听并处理处理失败的股票数据消息
 * 用于监控和故障排查
 */
@Slf4j
@Component
public class DeadLetterQueueListener {

    /**
     * 监听死信队列
     * 记录失败消息的详细信息，用于故障排查
     */
    @RabbitListener(queues = "stock.data.dlq")
    public void onDeadLetterMessage(Message message) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);

            log.error("========== 死信队列消息 ==========");
            log.error("消息ID: {}", message.getMessageProperties().getMessageId());
            log.error("消息内容: {}", messageBody);
            log.error("接收时间: {}", message.getMessageProperties().getTimestamp());
            log.error("重试次数: {}",
                    message.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0));
            log.error("原始交换机: {}", message.getMessageProperties().getReceivedExchange());
            log.error("原始路由键: {}", message.getMessageProperties().getReceivedRoutingKey());
            log.error("==================================");

            // 这里可以根据实际需求添加以下处理：
            // 1. 将失败消息持久化到数据库，便于后续分析和重试
            // 2. 发送告警通知（邮件、企业微信、钉钉等）
            // 3. 根据错误类型进行特殊处理
            // 4. 记录到监控系统

        } catch (Exception e) {
            log.error("处理死信队列消息异常: {}", e.getMessage(), e);
        }
    }
}
