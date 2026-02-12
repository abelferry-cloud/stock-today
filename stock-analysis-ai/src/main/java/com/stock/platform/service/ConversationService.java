package com.stock.platform.service;

import com.stock.platform.entity.Conversation;

import java.util.List;

/**
 * 对话记录服务接口
 */
public interface ConversationService {

    /**
     * 获取或创建对话记录
     */
    Conversation getOrCreateConversation(String conversationId);

    /**
     * 保存用户消息
     */
    void saveUserMessage(String conversationId, String content);

    /**
     * 保存AI回复
     */
    void saveAssistantMessage(String conversationId, String content);

    /**
     * 获取对话历史列表
     */
    List<Conversation> getConversationList(int limit);

    /**
     * 结束对话
     */
    void endConversation(String conversationId);
}
