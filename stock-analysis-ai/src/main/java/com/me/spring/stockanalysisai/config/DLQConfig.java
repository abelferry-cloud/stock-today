package com.me.spring.stockanalysisai.config;

import com.me.stock.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 死信队列配置类
 * 用于处理处理失败的股票数据消息
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DLQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange stockDataDlxExchange() {
        DirectExchange exchange = new DirectExchange(
                "stock.data.dlx.exchange",
                true,
                false
        );
        return exchange;
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue stockDataDlqQueue() {
        return QueueBuilder
                .durable("stock.data.dlq")
                .build();
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding bindingStockDataDlq() {
        Binding binding = BindingBuilder
                .bind(stockDataDlqQueue())
                .to(stockDataDlxExchange())
                .with("stock.data.dlq");
        log.info("死信队列绑定初始化: 队列[stock.data.dlq] -> 交换机[stock.data.dlx.exchange]");
        return binding;
    }

    /**
     * 主队列（带死信队列配置）
     */
    @Bean
    public Queue stockVectorQueueWithDlx() {
        Map<String, Object> args = Map.of(
                "x-dead-letter-exchange", "stock.data.dlx.exchange",
                "x-dead-letter-routing-key", "stock.data.dlq"
        );

        return QueueBuilder
                .durable(rabbitMQProperties.getVectorQueue())
                .withArguments(args)
                .build();
    }
}
