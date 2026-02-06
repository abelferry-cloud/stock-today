package com.stock.platform.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * DeepSeek Chat Service
 * Handles conversation with DeepSeek AI using Spring AI
 * Supports both regular and streaming responses
 */
@Service
public class DeepSeekService {

    private final ChatClient chatClient;

    public DeepSeekService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Send a simple message to DeepSeek and get a response
     *
     * @param message the message to send
     * @return the AI response
     */
    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * Send a message with system prompt to DeepSeek
     *
     * @param systemPrompt the system prompt
     * @param userMessage the user message
     * @return the AI response
     */
    public String chat(String systemPrompt, String userMessage) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * Send a message and get streaming response
     *
     * @param message the message to send
     * @return Flux stream of AI response chunks
     */
    public Flux<String> chatStream(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * Send a message with system prompt and get streaming response
     *
     * @param systemPrompt the system prompt
     * @param userMessage the user message
     * @return Flux stream of AI response chunks
     */
    public Flux<String> chatStream(String systemPrompt, String userMessage) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .stream()
                .content();
    }
}
