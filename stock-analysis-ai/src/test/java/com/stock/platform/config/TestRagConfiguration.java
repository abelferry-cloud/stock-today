package com.stock.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * RAG测试配置类
 * 仅在测试环境启用，用于启用VectorStore bean以测试Pinecone向量存储功能
 */
@Slf4j
@Configuration
@Profile("test")
public class TestRagConfiguration {

    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    private String pineconeApiKey;

    @Value("${spring.ai.vectorstore.pinecone.index-name}")
    private String pineconeIndexName;

    /**
     * 创建VectorStore Bean
     * 使用@Primary注解覆盖生产配置中被注释的VectorStore bean
     */
    @Bean
    @Primary
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        log.info("初始化Pinecone向量存储 (测试环境): indexName={}, apiKey={}",
                pineconeIndexName, maskApiKey(pineconeApiKey));
        return PineconeVectorStore.builder(embeddingModel)
                .apiKey(pineconeApiKey)
                .indexName(pineconeIndexName)
                .build();
    }

    /**
     * 隐藏API Key的敏感信息（仅用于日志输出）
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "***";
        }
        return apiKey.substring(0, 8) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}
