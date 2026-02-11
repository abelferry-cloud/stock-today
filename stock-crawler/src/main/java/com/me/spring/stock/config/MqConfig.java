package com.me.spring.stock.config;

import com.me.stock.config.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置类
 * 统一配置所有 RabbitMQ 队列、交换机和绑定关系
 */
@Configuration
public class MqConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;


    /**
     * JSON消息转换器
     * 使用 Jackson 进行消息序列化和反序列化
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate，使用 JSON 转换器
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    /**
     * 国内大盘信息队列
     */
    @Bean
    public Queue innerMarketQueue() {
        return new Queue(rabbitMQProperties.getInnerMarketQueue(), true);
    }

    /**
     * 国外大盘信息队列
     */
    @Bean
    public Queue outerMarketQueue() {
        return new Queue(rabbitMQProperties.getOuterMarketQueue(), true);
    }

    /**
     * 股票向量数据队列（RAG 知识库）
     */
    @Bean
    public Queue stockVectorQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getVectorQueue()).build();
    }

    /**
     * 股票原始数据队列（备用）
     */
    @Bean
    public Queue stockRawDataQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getRawDataQueue()).build();
    }

    // ==================== 交换机定义 ====================

    /**
     * 股票大盘交换机（Topic 类型）
     * 旧版交换机，保留兼容性
     */
    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(rabbitMQProperties.getStockExchange(), true, false);
    }

    /**
     * 股票数据主交换机（Topic 类型）
     * 用于路由所有股票相关数据
     */
    @Bean
    public TopicExchange stockDataExchange() {
        return new TopicExchange(rabbitMQProperties.getDataExchange(), true, false);
    }

    // ==================== 绑定关系 ====================

    /**
     * 绑定：国内大盘队列 -> 股票大盘交换机
     */
    @Bean
    public Binding bindingInnerMarketExchange() {
        return BindingBuilder.bind(innerMarketQueue())
                .to(stockExchange())
                .with(rabbitMQProperties.getInnerMarketRoutingKey());
    }

    /**
     * 绑定：国外大盘队列 -> 股票大盘交换机
     */
    @Bean
    public Binding bindingOuterMarketExchange() {
        return BindingBuilder.bind(outerMarketQueue())
                .to(stockExchange())
                .with(rabbitMQProperties.getOuterMarketRoutingKey());
    }

    /**
     * 绑定：向量数据队列 -> 数据主交换机
     * 用于 RAG 知识库向量数据处理
     */
    @Bean
    public Binding bindingStockVectorQueue() {
        return BindingBuilder.bind(stockVectorQueue())
                .to(stockDataExchange())
                .with(rabbitMQProperties.getVectorRoutingKey());
    }

    /**
     * 绑定：原始数据队列 -> 数据主交换机
     * 用于原始数据持久化（备用）
     */
    @Bean
    public Binding bindingStockRawDataQueue() {
        return BindingBuilder.bind(stockRawDataQueue())
                .to(stockDataExchange())
                .with(rabbitMQProperties.getRawDataRoutingKey());
    }
}
