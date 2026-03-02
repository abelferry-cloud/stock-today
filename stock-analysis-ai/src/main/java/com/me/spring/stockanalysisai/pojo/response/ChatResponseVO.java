package com.me.spring.stockanalysisai.pojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应VO
 * 
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天响应结果")
public class ChatResponseVO {

    /**
     * AI回复内容
     */
    @Schema(description = "AI回复内容", example = "市盈率是衡量股票价格是否合理的重要指标...")
    private String content;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "conv_123456")
    private String conversationId;

    /**
     * 是否流式输出
     */
    @Schema(description = "是否流式输出", example = "false")
    private Boolean isStream;
}
