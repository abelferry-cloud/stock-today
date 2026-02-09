package com.stock.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 聊天请求 DTO
 */
@Data
public class ChatRequest {

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 会话 ID（用于会话记忆）
     */
    private String conversationId;
}
