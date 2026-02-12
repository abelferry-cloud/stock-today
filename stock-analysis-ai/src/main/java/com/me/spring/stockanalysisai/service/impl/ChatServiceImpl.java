package com.me.spring.stockanalysisai.service.impl;

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
 * 
 * @author system
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    @Override
    public ChatResponseVO chat(ChatRequestDTO request) {
        try {
            String content = chatClient.prompt()
                    .user(request.getUserPrompt())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, request.getConversationId()))
                    .call()
                    .content();


            return ChatResponseVO.builder()
                    .content(content)
                    .conversationId(request.getConversationId())
                    .isStream(false)
                    .build();
                    
        } catch (Exception e) {
            log.error("聊天处理失败: conversationId={}, error={}", 
                    request.getConversationId(), e.getMessage(), e);
            throw new BusinessException("AI服务异常，请稍后重试", e);
        }
    }

    @Override
    public Flux<String> chatStream(ChatRequestDTO request) {
        try {
            return chatClient.prompt()
                    .user(request.getUserPrompt())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, request.getConversationId()))
                    .stream()
                    .content()
                    .doOnComplete(() -> log.info("流式聊天完成: conversationId={}", 
                            request.getConversationId()))
                    .doOnError(error -> log.error("流式聊天错误: conversationId={}, error={}", 
                            request.getConversationId(), error.getMessage(), error));
                    
        } catch (Exception e) {
            log.error("流式聊天处理失败: conversationId={}, error={}", 
                    request.getConversationId(), e.getMessage(), e);
            throw new BusinessException("AI服务异常，请稍后重试", e);
        }
    }
}
