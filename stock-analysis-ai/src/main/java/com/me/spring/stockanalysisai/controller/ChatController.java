package com.me.spring.stockanalysisai.controller;

import com.me.spring.stockanalysisai.common.Result;
import com.me.spring.stockanalysisai.pojo.request.ChatRequestDTO;
import com.me.spring.stockanalysisai.pojo.response.ChatResponseVO;
import com.me.spring.stockanalysisai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

/**
 * 聊天接口控制器
 * 
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Validated
@Tag(name = "ChatController", description = "聊天接口")
public class ChatController {

    private final ChatService chatService;

    /**
     * 普通聊天接口
     * 接收用户输入，返回AI回复
     * 
     * @param request 聊天请求参数
     * @return AI回复结果
     */
    @PostMapping
    @Operation(summary = "聊天接口", description = "接收用户输入，返回AI回复")
    public Result<ChatResponseVO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseVO response = chatService.chat(request);
        return Result.success(response);
    }

    /**
     * 流式聊天接口
     * 接收用户输入，流式返回AI回复
     * 
     * @param request 聊天请求参数
     * @return 流式AI回复
     */
    @PostMapping("/stream")
    @Operation(summary = "流式聊天接口", description = "接收用户输入，流式返回AI回复")
    public Flux<String> chatStream(@Valid @RequestBody ChatRequestDTO request) {
        return chatService.chatStream(request);
    }
}
