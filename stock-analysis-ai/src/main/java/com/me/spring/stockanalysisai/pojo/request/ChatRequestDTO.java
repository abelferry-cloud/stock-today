package com.me.spring.stockanalysisai.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 聊天请求DTO
 * 
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Schema(description = "聊天请求参数")
public class ChatRequestDTO {

    /**
     * 用户输入的消息
     */
    @NotBlank(message = "用户消息不能为空")
    @Size(max = 2000, message = "用户消息长度不能超过2000个字符")
    @Schema(description = "用户消息", required = true, example = "什么是市盈率？")
    private String userPrompt;

    /**
     * 会话ID，用于保持多轮对话上下文
     */
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", required = true, example = "conv_123456")
    private String conversationId;

    /**
     * 是否启用流式输出
     */
    @Schema(description = "是否启用流式输出", example = "true")
    private Boolean stream = true;
}
