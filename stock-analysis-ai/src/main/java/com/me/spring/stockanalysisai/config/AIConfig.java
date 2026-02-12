package com.me.spring.stockanalysisai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel, VectorStore vectorStore){
        return ChatClient.builder(chatModel)
                .defaultSystem("你叫小财，是一个有用的AI助理，可以回答关于股票的所有问题")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().maxMessages(10).build()).build()
                )
                .build();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();

        List<Document> documents = List.of(new Document("腾讯最新的股价为1902每股"));

        simpleVectorStore.add(documents);

        return simpleVectorStore;
    }
}
