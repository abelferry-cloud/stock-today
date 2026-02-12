package com.me.spring.stockanalysisai.config;

import com.me.stock.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 用于消费来自 stock-crawler 的股票数据消息
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    /**
     * JSON 消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    /**
     * 股票向量数据队列
     * 用于接收来自 stock-crawler 的股票数据，用于 RAG 知识库向量处理
     */
    @Bean
    public Queue stockVectorQueue() {
        Queue queue = QueueBuilder
                .durable(rabbitMQProperties.getVectorQueue())
                .build();
        log.info("RabbitMQ 队列初始化: {}", rabbitMQProperties.getVectorQueue());
        return queue;
    }

    /**
     * 股票数据主交换机（Topic 类型）
     */
    @Bean
    public TopicExchange stockDataExchange() {
        TopicExchange exchange = new TopicExchange(
                rabbitMQProperties.getDataExchange(),
                true,  // durable
                false  // autoDelete
        );
        log.info("RabbitMQ 交换机初始化: {}", rabbitMQProperties.getDataExchange());
        return exchange;
    }

    /**
     * 绑定：向量数据队列 -> 数据主交换机
     * 用于接收股票数据并更新 RAG 知识库
     */
    @Bean
    public Binding bindingStockVectorQueue() {
        Binding binding = BindingBuilder
                .bind(stockVectorQueue())
                .to(stockDataExchange())
                .with(rabbitMQProperties.getVectorRoutingKey());
        log.info("RabbitMQ 绑定初始化: 队列[{}] -> 交换机[{}] -> 路由键[{}]",
                rabbitMQProperties.getVectorQueue(),
                rabbitMQProperties.getDataExchange(),
                rabbitMQProperties.getVectorRoutingKey());
        return binding;
    }
}
