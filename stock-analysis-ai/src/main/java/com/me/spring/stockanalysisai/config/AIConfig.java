package com.me.spring.stockanalysisai.config;

import com.me.spring.stockanalysisai.common.Constants;
import com.me.spring.stockanalysisai.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * AI配置类
 * 
 * @author system
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AIConfig {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 配置ChatClient
     * 
     * @param chatModel 聊天模型
     * @param vectorStore 向量存储
     * @return ChatClient实例
     */
    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel, VectorStore vectorStore) {
        String systemPrompt = knowledgeBaseService.loadSystemPrompt();
        
        log.info("系统提示词已加载....");
        
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(
                                MessageWindowChatMemory.builder()
                                        .maxMessages(Constants.MAX_HISTORY_MESSAGES)
                                        .build()
                        ).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(Constants.SIMILARITY_THRESHOLD)
                                        .topK(Constants.TOP_K)
                                        .build())
                                .build()
                )
                .build();
    }

    /**
     * 配置向量存储, 测试环境下使用SimpleVectorStore
     * 
     * @param embeddingModel 嵌入模型
     * @return VectorStore 实例
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // 从资源目录加载知识库文档
        List<Document> knowledgeDocuments = knowledgeBaseService.loadAllDocuments();

        // 将文档添加到向量存储
        simpleVectorStore.add(knowledgeDocuments);

        log.info("知识库初始化完成，已添加文档数量: {}", knowledgeDocuments.size());

        return simpleVectorStore;
    }

    /**
     * 配置内存向量存储
     * 使用 InMemoryVectorStore 作为向量存储实现
     * 注意：生产环境建议使用持久化的向量数据库（如 Milvus、Weaviate、Pinecone 等）
     */
//    @Bean
//    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel){
//        return PineconeVectorStore.builder(embeddingModel)
//                .apiKey("<KEY>")
//                .indexName("stock-analysis-ai")
//                .build();
//    }
}

