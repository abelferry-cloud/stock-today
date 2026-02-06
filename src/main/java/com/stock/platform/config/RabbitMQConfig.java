package com.stock.platform.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Configures message queues, exchanges, and bindings for asynchronous processing
 */
@Configuration
public class RabbitMQConfig {

    // Queue Names
    public static final String STOCK_DATA_QUEUE = "stock.data.queue";
    public static final String RAG_BUILD_QUEUE = "rag.build.queue";
    public static final String STOCK_CRAWL_QUEUE = "stock.crawl.queue";

    // Exchange Names
    public static final String STOCK_EXCHANGE = "stock.exchange";
    public static final String RAG_EXCHANGE = "rag.exchange";

    // Routing Keys
    public static final String STOCK_DATA_ROUTING_KEY = "stock.data";
    public static final String RAG_BUILD_ROUTING_KEY = "rag.build";
    public static final String STOCK_CRAWL_ROUTING_KEY = "stock.crawl";

    /**
     * Stock Data Queue - for processing stock data
     */
    @Bean
    public Queue stockDataQueue() {
        return QueueBuilder.durable(STOCK_DATA_QUEUE)
            .withArgument("x-max-length", 10000)
            .build();
    }

    /**
     * RAG Build Queue - for building RAG knowledge base asynchronously
     */
    @Bean
    public Queue ragBuildQueue() {
        return QueueBuilder.durable(RAG_BUILD_QUEUE)
            .withArgument("x-max-length", 5000)
            .build();
    }

    /**
     * Stock Crawl Queue - for crawling stock data
     */
    @Bean
    public Queue stockCrawlQueue() {
        return QueueBuilder.durable(STOCK_CRAWL_QUEUE)
            .withArgument("x-max-length", 1000)
            .build();
    }

    /**
     * Stock Exchange - Direct Exchange for stock-related messages
     */
    @Bean
    public DirectExchange stockExchange() {
        return new DirectExchange(STOCK_EXCHANGE, true, false);
    }

    /**
     * RAG Exchange - Topic Exchange for RAG-related messages
     */
    @Bean
    public TopicExchange ragExchange() {
        return new TopicExchange(RAG_EXCHANGE, true, false);
    }

    /**
     * Binding: Stock Data Queue to Stock Exchange
     */
    @Bean
    public Binding stockDataBinding() {
        return BindingBuilder.bind(stockDataQueue())
            .to(stockExchange())
            .with(STOCK_DATA_ROUTING_KEY);
    }

    /**
     * Binding: RAG Build Queue to RAG Exchange
     */
    @Bean
    public Binding ragBuildBinding() {
        return BindingBuilder.bind(ragBuildQueue())
            .to(ragExchange())
            .with(RAG_BUILD_ROUTING_KEY);
    }

    /**
     * Binding: Stock Crawl Queue to Stock Exchange
     */
    @Bean
    public Binding stockCrawlBinding() {
        return BindingBuilder.bind(stockCrawlQueue())
            .to(stockExchange())
            .with(STOCK_CRAWL_ROUTING_KEY);
    }

    /**
     * JSON Message Converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
