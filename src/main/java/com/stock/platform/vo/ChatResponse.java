package com.stock.platform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat Response VO
 * Response object for chat endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Chat response")
public class ChatResponse {

    @Schema(description = "AI response message")
    private String reply;

    @Schema(description = "Response timestamp")
    private Long timestamp;

    public static ChatResponse of(String reply) {
        return new ChatResponse(reply, System.currentTimeMillis());
    }
}
