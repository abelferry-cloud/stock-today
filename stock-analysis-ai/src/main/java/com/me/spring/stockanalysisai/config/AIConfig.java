package com.me.spring.stockanalysisai.config;

import com.me.spring.stockanalysisai.common.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;

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

    private final ResourceLoader resourceLoader;

    /**
     * 配置ChatClient
     *
     * @param chatModel 聊天模型
     * @param vectorStore 向量存储 (Pinecone)
     * @return ChatClient实例
     */
    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel, VectorStore vectorStore) {
        String systemPrompt = loadSystemPrompt();

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
     * 加载系统提示词
     */
    private String loadSystemPrompt() {
        try {
            Resource systemPromptResource = resourceLoader.getResource(Constants.SYSTEM_PROMPT_PATH);
            String systemPrompt = new String(
                    systemPromptResource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return cleanMarkdownToText(systemPrompt);

        } catch (Exception e) {
            log.error("加载系统提示词失败", e);
            throw new RuntimeException("加载系统提示词失败", e);
        }
    }

    /**
     * 清理Markdown格式，提取纯文本内容用于系统提示词
     * 移除标题标记和格式符号，保留核心内容
     */
    private String cleanMarkdownToText(String markdown) {
        // 移除标题标记（##）
        String text = markdown.replaceAll("(?m)^#{1,6}\\s+", "");

        // 移除粗体标记（**）
        text = text.replaceAll("\\*\\*", "");

        // 移除斜体标记（*）
        text = text.replaceAll("(?<!\\*)\\*(?!\\*)", "");

        // 移除代码块标记（```）
        text = text.replaceAll("```[a-z]*\\n?", "");
        text = text.replaceAll("```", "");

        // 移除分隔线（---）
        text = text.replaceAll("---", "");

        // 压缩多个空行为单个空行
        text = text.replaceAll("\\n{3,}", "\n\n");

        // 移除首尾空白
        text = text.trim();

        return text;
    }
}


