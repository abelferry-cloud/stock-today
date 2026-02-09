package com.stock.platform.dto.response;

import com.stock.platform.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailResponse {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 对话标题
     */
    private String title;

    /**
     * 用户消息数
     */
    private Integer userMessageCount;

    /**
     * AI回复数
     */
    private Integer aiMessageCount;

    /**
     * 状态：1-进行中，2-已结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 消息列表
     */
    private List<Message> messages;
}
