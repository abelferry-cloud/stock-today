package com.me.spring.stockanalysisai.service.impl;

import com.me.spring.stockanalysisai.config.ApiKeyManager;
import com.me.spring.stockanalysisai.pojo.request.ChatRequestDTO;
import com.me.spring.stockanalysisai.pojo.response.ChatResponseVO;
import com.me.spring.stockanalysisai.exception.BusinessException;
import com.me.spring.stockanalysisai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ApiKeyManager apiKeyManager;

    @Override
    public ChatResponseVO chat(ChatRequestDTO request) {
        try {
            String content = chatClient.prompt()
                    .user(request.getUserPrompt())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, request.getConversationId()))
                    .call()
                    .content();

            // 成功时重置所有 Key 的冷却状态
            if (apiKeyManager.getApiKeys() != null) {
                apiKeyManager.getApiKeys().forEach(apiKeyManager::resetKey);
            }

            return ChatResponseVO.builder()
                    .content(content)
                    .conversationId(request.getConversationId())
                    .isStream(false)
                    .build();

        } catch (Exception e) {
            log.error("聊天处理失败: conversationId={}, error={}",
                    request.getConversationId(), e.getMessage(), e);

            // 检查是否是限流错误 (429)，记录以便监控
            if (isRateLimitError(e)) {
                log.warn("API Key 触发限流 (429), Key 状态: {}",
                        apiKeyManager.getKeyStatusInfo());
                // 标记当前 key 进入冷却（如果使用多 key）
                if (apiKeyManager.getApiKeys() != null && !apiKeyManager.getApiKeys().isEmpty()) {
                    apiKeyManager.markCooling(apiKeyManager.getApiKeys().get(0));
                }
                throw new BusinessException("AI服务限流，请稍后重试", e);
            }

            throw new BusinessException("AI服务异常，请稍后重试", e);
        }
    }

    @Override
    public Flux<String> chatStream(ChatRequestDTO request) {
        return chatClient.prompt()
                .user(request.getUserPrompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, request.getConversationId()))
                .stream()
                .content()
                .doOnComplete(() -> log.info("流式聊天完成: conversationId={}",
                        request.getConversationId()))
                .doOnError(error -> {
                    if (isRateLimitError(error)) {
                        log.warn("流式聊天触发限流, Key 状态: {}", apiKeyManager.getKeyStatusInfo());
                        // 标记当前 key 进入冷却
                        if (apiKeyManager.getApiKeys() != null && !apiKeyManager.getApiKeys().isEmpty()) {
                            apiKeyManager.markCooling(apiKeyManager.getApiKeys().get(0));
                        }
                    }
                    log.error("流式聊天错误: conversationId={}, error={}",
                            request.getConversationId(), error.getMessage(), error);
                });
    }

    /**
     * 检查是否是限流错误 (429)
     */
    private boolean isRateLimitError(Throwable error) {
        String message = error.getMessage();
        if (message == null) {
            // 检查 cause
            if (error.getCause() != null) {
                message = error.getCause().getMessage();
            }
            if (message == null) {
                return false;
            }
        }

        // 检查各种可能的 429 错误标识
        return message.contains("429")
                || message.contains("Too Many Requests")
                || message.contains("rate limit")
                || message.contains("rate_limit")
                || message.contains("quota exceed")
                || message.toLowerCase().contains("api rate limit");
    }
}
