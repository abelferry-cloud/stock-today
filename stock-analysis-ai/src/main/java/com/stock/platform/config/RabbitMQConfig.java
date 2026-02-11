package com.stock.platform.config;

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
 * RabbitMQ配置类
 * 配置队列、交换机、绑定关系，统一使用 RabbitMQProperties 配置
 */
@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    // ==================== Bean定义 ====================

    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate，使用JSON转换器
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 股票向量存储队列
     * stock-crawler爬取数据后，将数据发送到此队列，当前模块消费并存入Pinecone向量数据库
     */
    @Bean
    public Queue stockVectorQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getVectorQueue())
                .build();
    }

    /**
     * 股票原始数据队列（备用）
     */
    @Bean
    public Queue stockRawDataQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getRawDataQueue())
                .build();
    }

    /**
     * 股票数据交换机（Topic类型）
     */
    @Bean
    public TopicExchange stockDataExchange() {
        return new TopicExchange(rabbitMQProperties.getDataExchange(), true, false);
    }

    /**
     * 绑定：向量队列 -> 交换机
     */
    @Bean
    public Binding stockVectorBinding() {
        return BindingBuilder.bind(stockVectorQueue())
                .to(stockDataExchange())
                .with(rabbitMQProperties.getVectorRoutingKey());
    }

    /**
     * 绑定：原始数据队列 -> 交换机
     */
    @Bean
    public Binding stockRawDataBinding() {
        return BindingBuilder.bind(stockRawDataQueue())
                .to(stockDataExchange())
                .with(rabbitMQProperties.getRawDataRoutingKey());
    }
}
