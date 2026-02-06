package com.stock.platform.service;

import com.stock.platform.common.ResultCode;
import com.stock.platform.dto.ChatRequest;
import com.stock.platform.vo.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Chat Service
 * Business logic for chat functionality
 * Handles validation, logging, and coordinates with DeepSeekService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final DeepSeekService deepSeekService;

    /**
     * Validate chat request
     *
     * @param request the chat request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRequest(ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Chat message cannot be empty");
        }

        if (request.getMessage().length() > 10000) {
            throw new IllegalArgumentException("Chat message is too long (max 10000 characters)");
        }
    }

    /**
     * Handle chat request (non-streaming)
     *
     * @param request the chat request
     * @return chat response
     */
    public ChatResponse chat(ChatRequest request) {
        log.info("Processing chat request: {}", request.getMessage());

        try {
            // Validate request
            validateRequest(request);

            // Call DeepSeek service
            String reply;
            if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
                reply = deepSeekService.chat(request.getSystemPrompt(), request.getMessage());
            } else {
                reply = deepSeekService.chat(request.getMessage());
            }

            log.info("Chat response received, length: {}", reply.length());
            return ChatResponse.of(reply);

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error in chat service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get response from AI: " + e.getMessage(), e);
        }
    }

    /**
     * Handle chat request (streaming)
     *
     * @param request the chat request
     * @return Flux stream of chat response chunks
     */
    public Flux<String> chatStream(ChatRequest request) {
        log.info("Processing streaming chat request: {}", request.getMessage());

        try {
            // Validate request
            validateRequest(request);

            // Create streaming response
            Flux<String> stream;
            if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
                stream = deepSeekService.chatStream(request.getSystemPrompt(), request.getMessage());
            } else {
                stream = deepSeekService.chatStream(request.getMessage());
            }

            log.info("Streaming chat initiated");
            return stream
                    .doOnComplete(() -> log.info("Streaming chat completed"))
                    .doOnError(error -> log.error("Streaming chat error: {}", error.getMessage(), error));

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return Flux.error(e);
        } catch (Exception e) {
            log.error("Error in streaming chat service: {}", e.getMessage(), e);
            return Flux.error(new RuntimeException("Failed to get streaming response from AI: " + e.getMessage(), e));
        }
    }

    /**
     * Handle simple chat (for GET requests)
     *
     * @param message the message
     * @return chat response
     */
    public ChatResponse chatSimple(String message) {
        ChatRequest request = new ChatRequest();
        request.setMessage(message);
        return chat(request);
    }

    /**
     * Handle test chat with predefined message
     *
     * @return chat response
     */
    public ChatResponse chatTest() {
        String testMessage = "hello ,My name is Jovan,nice to meet u.";
        log.info("Executing test chat with message: {}", testMessage);

        ChatRequest request = new ChatRequest();
        request.setMessage(testMessage);
        return chat(request);
    }
}
