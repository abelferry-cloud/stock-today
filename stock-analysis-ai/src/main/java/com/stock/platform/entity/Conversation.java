package com.stock.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 对话记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    /**
     * 主键ID
     */
    private Long id;

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
}
