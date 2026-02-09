package com.stock.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 角色：user-用户，assistant-AI助手，system-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * Token数量（可选）
     */
    private Integer tokens;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
