package com.stock.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SpringAIConfig {

    @Value("${spring.ai.deepseek.base-url}")
    private String baseUrl;

    /**
     * 创建 ChatClient
     * 使用 Spring Boot 自动配置的 ChatModel Bean
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, ApiKeyRotationManager rotationManager) {
        log.info("初始化 ChatClient: baseUrl={}, apiKeysCount={}",
                baseUrl, rotationManager.getTotalKeys());
        return ChatClient.builder(chatModel).build();
    }

    /**
     * 配置会话记忆（使用 MessageWindowChatMemory）
     * 保留最近 100 条消息
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
    }

    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    private String pineconeApiKey;

    @Value("${spring.ai.vectorstore.pinecone.index-name:stock-analysis}")
    private String pineconeIndexName;

    /**
     * 配置Pinecone向量存储
     * 用于存储股票数据的向量表示，实现RAG知识库
     */
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        log.info("初始化Pinecone向量存储: indexName={}", pineconeIndexName);
        return PineconeVectorStore.builder(embeddingModel)
                .apiKey(pineconeApiKey)
                .indexName(pineconeIndexName)
                .build();
    }
}
