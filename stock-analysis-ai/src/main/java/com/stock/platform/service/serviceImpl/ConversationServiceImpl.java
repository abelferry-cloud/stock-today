package com.stock.platform.service.serviceImpl;

import com.stock.platform.pojo.entity.Conversation;
import com.stock.platform.pojo.entity.Message;
import com.stock.platform.mapper.ConversationMapper;
import com.stock.platform.mapper.MessageMapper;
import com.stock.platform.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 对话记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public Conversation getOrCreateConversation(String conversationId) {
        Conversation conversation = conversationMapper.selectByConversationId(conversationId);

        if (conversation == null) {
            // 创建新对话
            conversation = Conversation.builder()
                    .conversationId(conversationId)
                    .title(null)  // 可以后续根据第一条消息生成标题
                    .userMessageCount(0)
                    .aiMessageCount(0)
                    .status(1)  // 进行中
                    .build();

            conversationMapper.insert(conversation);
            log.info("创建新对话: conversationId={}", conversationId);
        }

        return conversation;
    }

    @Override
    @Transactional
    public void saveUserMessage(String conversationId, String content) {
        // 先确保会话记录存在
        getOrCreateConversation(conversationId);

        // 保存用户消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .role("user")
                .content(content)
                .tokens(null)
                .build();

        messageMapper.insert(message);

        // 更新用户消息计数
        conversationMapper.updateMessageCount(conversationId, 1, 0);

        log.debug("保存用户消息: conversationId={}, contentLength={}",
                conversationId, content.length());
    }

    @Override
    @Transactional
    public void saveAssistantMessage(String conversationId, String content) {
        // 先确保会话记录存在
        getOrCreateConversation(conversationId);

        // 保存AI回复
        Message message = Message.builder()
                .conversationId(conversationId)
                .role("assistant")
                .content(content)
                .tokens(null)
                .build();

        messageMapper.insert(message);

        // 更新AI消息计数
        conversationMapper.updateMessageCount(conversationId, 0, 1);

        log.debug("保存AI回复: conversationId={}, contentLength={}",
                conversationId, content.length());
    }

    @Override
    public List<Conversation> getConversationList(int limit) {
        return conversationMapper.selectListByLimit(limit);
    }

    @Override
    @Transactional
    public void endConversation(String conversationId) {
        conversationMapper.updateStatus(conversationId, 2);
        log.info("结束对话: conversationId={}", conversationId);
    }
}
