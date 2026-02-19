package com.me.spring.stockanalysisai.controller;

import com.me.spring.stockanalysisai.common.Result;
import com.me.spring.stockanalysisai.common.ResultCode;
import com.me.stock.config.RabbitMQProperties;
import com.me.stock.pojo.dto.StockDataMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息队列管理控制器
 * 用于查询和管理 RabbitMQ 队列状态
 */
@Slf4j
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
@Tag(name = "消息队列管理", description = "消息队列状态查询和管理接口")
public class MQController {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitMQProperties rabbitMQProperties;

    /**
     * 获取 RabbitMQ 连接状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取RabbitMQ连接状态", description = "查询RabbitMQ服务是否正常连接")
    public Result<Map<String, Object>> getRabbitMQStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            // 检查连接
            boolean isConnected = rabbitTemplate.getConnectionFactory().createConnection().isOpen();
            status.put("connected", isConnected);

            // 获取配置信息
            status.put("host", rabbitTemplate.getConnectionFactory().getHost());
            status.put("port", rabbitTemplate.getConnectionFactory().getPort());
            status.put("virtualHost", rabbitTemplate.getConnectionFactory().getVirtualHost());

            if (isConnected) {
                return Result.success(status);
            } else {
                return Result.error(ResultCode.SERVICE_UNAVAILABLE.getCode(), "RabbitMQ连接失败");
            }

        } catch (Exception e) {
            log.error("获取RabbitMQ状态失败: {}", e.getMessage(), e);
            return Result.error(ResultCode.SERVICE_UNAVAILABLE.getCode(), "RabbitMQ服务不可用: " + e.getMessage());
        }
    }

    /**
     * 获取队列信息
     */
    @GetMapping("/queues/info")
    @Operation(summary = "获取队列信息", description = "查询所有队列的基本信息")
    public Result<Map<String, Object>> getQueueInfo() {
        try {
            Map<String, Object> queueInfo = new HashMap<>();

            // 添加向量队列信息
            queueInfo.put("vectorQueue", rabbitMQProperties.getVectorQueue());
            queueInfo.put("dlxQueue", "stock.data.dlq");
            queueInfo.put("exchange", rabbitMQProperties.getDataExchange());
            queueInfo.put("routingKey", rabbitMQProperties.getVectorRoutingKey());

            // 尝试获取队列属性
            try {
                java.util.Properties queueProps = rabbitAdmin.getQueueProperties(rabbitMQProperties.getVectorQueue());
                // 将Properties转换为Map
                Map<String, Object> propsMap = new HashMap<>();
                queueProps.forEach((key, value) -> propsMap.put(String.valueOf(key), value));
                queueInfo.put("queueProperties", propsMap);
            } catch (Exception e) {
                log.warn("获取队列属性失败: {}", e.getMessage());
            }

            return Result.success(queueInfo);

        } catch (Exception e) {
            log.error("获取队列信息失败: {}", e.getMessage(), e);
            return Result.error(ResultCode.INTERNAL_ERROR.getCode(), "获取队列信息失败: " + e.getMessage());
        }
    }

    /**
     * 测试消息发送
     */
    @GetMapping("/test/send")
    @Operation(summary = "测试消息发送", description = "发送测试消息到队列")
    public Result<String> testSendMessage() {
        try {
            StockDataMessage testMessage = StockDataMessage.builder()
                    .messageId(UUID.randomUUID().toString())
                    .stockCode("TEST001")
                    .stockName("测试股票")
                    .dataType("TEST")
                    .title("测试消息")
                    .content("这是一条测试消息，用于验证消息队列是否正常工作")
                    .publishTime(LocalDateTime.now())
                    .source("测试")
                    .createTime(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    rabbitMQProperties.getDataExchange(),
                    rabbitMQProperties.getVectorRoutingKey(),
                    testMessage
            );

            log.info("测试消息发送成功: messageId={}", testMessage.getMessageId());
            return Result.success("测试消息发送成功, messageId: " + testMessage.getMessageId());

        } catch (Exception e) {
            log.error("发送测试消息失败: {}", e.getMessage(), e);
            return Result.error(ResultCode.INTERNAL_ERROR.getCode(), "发送测试消息失败: " + e.getMessage());
        }
    }
}

