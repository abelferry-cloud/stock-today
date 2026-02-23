package com.me.spring.stockanalysisai.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 声明队列、交换机、绑定关系
 */
@Configuration
public class RabbitMQConfig {

    @Value("${stock.rabbitmq.vector-queue:stock.vector.queue}")
    private String vectorQueue;

    @Value("${stock.rabbitmq.data-exchange:stock.data.exchange}")
    private String dataExchange;

    @Value("${stock.rabbitmq.vector-routing-key:stock.data.vector}")
    private String vectorRoutingKey;

    @Value("${stock.rabbitmq.dlx-exchange:stock.data.dlx.exchange}")
    private String dlxExchange;

    @Value("${stock.rabbitmq.dlx-routing-key:stock.data.dlx}")
    private String dlxRoutingKey;

    @Value("${stock.rabbitmq.dlq-queue:stock.vector.dlq}")
    private String dlqQueue;

    // 缓存失效队列配置
    @Value("${stock.cache.invalid.queue:stock.cache.invalid.queue}")
    private String cacheInvalidQueue;

    @Value("${stock.cache.invalid.exchange:stock.cache.invalid.exchange}")
    private String cacheInvalidExchange;

    @Value("${stock.cache.invalid.routing-key:cache.invalid}")
    private String cacheInvalidRoutingKey;

    /**
     * 声明向量数据队列（支持死信队列）
     */
    @Bean
    public Queue vectorQueue() {
        return QueueBuilder.durable(vectorQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", dlxRoutingKey)
                .build();
    }

    /**
     * 声明死信交换机（Direct类型）
     */
    @Bean
    public Exchange dlxExchange() {
        return ExchangeBuilder.directExchange(dlxExchange).durable(true).build();
    }

    /**
     * 声明死信队列
     */
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqQueue).build();
    }

    /**
     * 声明死信队列与死信交换机的绑定
     */
    @Bean
    public Binding dlqBinding(Queue dlqQueue, Exchange dlxExchange) {
        return BindingBuilder.bind(dlqQueue).to(dlxExchange).with(dlxRoutingKey).noargs();
    }

    /**
     * 声明数据交换机（Topic类型）
     */
    @Bean
    public Exchange dataExchange() {
        return ExchangeBuilder.topicExchange(dataExchange).durable(true).build();
    }

    /**
     * 声明向量数据队列与交换机的绑定
     */
    @Bean
    public Binding vectorBinding(Queue vectorQueue, Exchange dataExchange) {
        return BindingBuilder.bind(vectorQueue).to(dataExchange).with(vectorRoutingKey).noargs();
    }

    /**
     * 声明缓存失效队列
     */
    @Bean
    public Queue cacheInvalidQueue() {
        return QueueBuilder.durable(cacheInvalidQueue).build();
    }

    /**
     * 声明缓存失效交换机（Direct类型）
     */
    @Bean
    public Exchange cacheInvalidExchange() {
        return ExchangeBuilder.directExchange(cacheInvalidExchange).durable(true).build();
    }

    /**
     * 声明缓存失效队列与交换机的绑定
     */
    @Bean
    public Binding cacheInvalidBinding(Queue cacheInvalidQueue, Exchange cacheInvalidExchange) {
        return BindingBuilder.bind(cacheInvalidQueue).to(cacheInvalidExchange).with(cacheInvalidRoutingKey).noargs();
    }

    /**
     * 配置 JSON 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate 使用 JSON 消息转换器
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}