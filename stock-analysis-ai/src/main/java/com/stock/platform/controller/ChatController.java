package com.stock.platform.controller;

import com.stock.platform.common.result.Result;
import com.stock.platform.dto.request.ChatRequest;
import com.stock.platform.dto.response.ChatResponse;
import com.stock.platform.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 聊天控制器
 */
@Schema(description = "聊天接口")
@Tag(name = "聊天管理", description = "提供AI对话功能，包括普通对话和流式对话")
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 普通对话接口
     * POST /api/chat/send
     */
    @Operation(
            summary = "发送聊天消息",
            description = "发送消息给AI，返回完整的响应内容",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功返回AI回复",
                            content = @Content(schema = @Schema(implementation = ChatResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "请求参数错误"),
                    @ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    @PostMapping("/send")
    public Result<ChatResponse> send(
            @Parameter(
                    description = "聊天请求参数",
                    required = true,
                    schema = @Schema(implementation = ChatRequest.class)
            )
            @Valid @RequestBody ChatRequest request) {
        log.info("接收到聊天请求: message={}", request.getMessage());
        ChatResponse response = chatService.chat(request);
        return Result.success(response);
    }

    /**
     * 流式对话接口
     * POST /api/chat/stream
     */
    @Operation(
            summary = "流式聊天",
            description = "以流式方式返回AI响应，适用于长文本生成场景",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功返回流式AI响应",
                            content = @Content(schema = @Schema(type = "string", format = "text/event-stream"))
                    ),
                    @ApiResponse(responseCode = "400", description = "请求参数错误"),
                    @ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    @PostMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> stream(
            @Parameter(
                    description = "聊天请求参数",
                    required = true,
                    schema = @Schema(implementation = ChatRequest.class)
            )
            @Valid @RequestBody ChatRequest request) {
        log.info("接收到流式聊天请求: message={}", request.getMessage());
        return chatService.chatStream(request);
    }
}
