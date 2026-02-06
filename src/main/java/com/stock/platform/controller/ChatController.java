package com.stock.platform.controller;

import com.stock.platform.common.Result;
import com.stock.platform.dto.ChatRequest;
import com.stock.platform.service.ChatService;
import com.stock.platform.vo.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Chat Controller
 * REST API for chatting with DeepSeek AI
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "Endpoints for AI chat functionality")
public class ChatController {

    private final ChatService chatService;

    /**
     * Chat endpoint with streaming response
     *
     * @param request the chat request
     * @return streaming response
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send message to DeepSeek AI (streaming)", description = "Send a message and get streaming AI response")
    public Flux<String> chatStream(@Valid @RequestBody ChatRequest request) {
        log.info("Received streaming chat request");
        return chatService.chatStream(request);
    }

    /**
     * Chat endpoint with non-streaming response
     *
     * @param request the chat request
     * @return the AI response
     */
    @PostMapping
    @Operation(summary = "Send message to DeepSeek AI", description = "Send a message and get AI response")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request");
        return Result.success(chatService.chat(request));
    }

    /**
     * Simple GET endpoint for testing with query parameter
     *
     * @param message the message to send
     * @return the AI response
     */
    @GetMapping
    @Operation(summary = "Send message via GET request", description = "Simple GET endpoint for testing")
    public Result<ChatResponse> chatGet(@RequestParam String message) {
        log.info("Received chat GET request");
        return Result.success(chatService.chatSimple(message));
    }

    /**
     * Test endpoint with predefined message
     *
     * @return the AI response
     */
    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Test endpoint with predefined message")
    public Result<ChatResponse> test() {
        log.info("Received test chat request");
        return Result.success(chatService.chatTest());
    }
}
