package com.me.spring.stockanalysisai.service;

import com.me.spring.stockanalysisai.pojo.request.ChatRequestDTO;
import com.me.spring.stockanalysisai.pojo.response.ChatResponseVO;
import reactor.core.publisher.Flux;

/**
 * 聊天服务接口
 * 
 * @author system
 * @since 1.0.0
 */
public interface ChatService {

    /**
     * 普通聊天
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponseVO chat(ChatRequestDTO request);

    /**
     * 流式聊天
     * 
     * @param request 聊天请求
     * @return 流式响应
     */
    Flux<String> chatStream(ChatRequestDTO request);
}
