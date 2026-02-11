package com.stock.platform.mapper;

import com.stock.platform.pojo.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 对话记录 Mapper
 */
@Mapper
public interface ConversationMapper {

    /**
     * 根据会话ID查询
     */
    Conversation selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 插入对话记录
     */
    int insert(Conversation conversation);

    /**
     * 更新对话记录
     */
    int update(Conversation conversation);

    /**
     * 更新消息计数
     */
    int updateMessageCount(@Param("conversationId") String conversationId,
                          @Param("userDelta") Integer userDelta,
                          @Param("aiDelta") Integer aiDelta);

    /**
     * 查询用户的对话列表（按时间倒序）
     */
    List<Conversation> selectListByLimit(@Param("limit") int limit);

    /**
     * 更新对话状态
     */
    int updateStatus(@Param("conversationId") String conversationId,
                    @Param("status") Integer status);
}
