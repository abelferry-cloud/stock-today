package com.stock.platform.mapper;

import com.stock.platform.pojo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息记录 Mapper
 */
@Mapper
public interface MessageMapper {

    /**
     * 插入消息记录
     */
    int insert(Message message);

    /**
     * 根据会话ID查询消息列表
     */
    List<Message> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID查询消息列表（按时间倒序）
     */
    List<Message> selectByConversationIdWithLimit(@Param("conversationId") String conversationId,
                                                   @Param("limit") int limit);

    /**
     * 统计会话消息数
     */
    int countByConversationId(@Param("conversationId") String conversationId);
}
