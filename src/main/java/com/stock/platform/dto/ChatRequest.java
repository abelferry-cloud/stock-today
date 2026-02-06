package com.stock.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Chat Request DTO
 * Request object for chat endpoint
 */
@Data
@Schema(description = "Chat request")
public class ChatRequest {

    @NotBlank(message = "Message cannot be blank")
    @Schema(description = "User message", example = "hello, My name is Jovan, nice to meet u.", required = true)
    private String message;

    @Schema(description = "System prompt (optional)", example = "You are a helpful assistant.")
    private String systemPrompt;
}
