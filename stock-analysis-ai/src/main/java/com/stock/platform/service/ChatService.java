package com.stock.platform.service;

import com.stock.platform.pojo.dto.request.ChatRequest;
import com.stock.platform.pojo.dto.response.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * 聊天服务接口
 */
public interface ChatService {

    /**
     * 普通对话（非流式）
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式对话
     */
    Flux<String> chatStream(ChatRequest request);
}
