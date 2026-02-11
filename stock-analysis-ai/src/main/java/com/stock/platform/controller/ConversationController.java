package com.stock.platform.controller;

import com.stock.platform.common.result.Result;
import com.stock.platform.pojo.dto.response.ConversationDetailResponse;
import com.stock.platform.pojo.entity.Conversation;
import com.stock.platform.pojo.entity.Message;
import com.stock.platform.mapper.MessageMapper;
import com.stock.platform.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话记录控制器
 */
@Schema(description = "对话记录管理接口")
@Tag(name = "对话记录管理", description = "管理对话历史记录，包括查询对话列表、对话详情、结束对话等")
@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    /**
     * 获取对话列表
     * GET /api/conversations?limit=10
     */
    @Operation(
            summary = "获取对话列表",
            description = "获取用户的对话列表，按时间倒序排列",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功返回对话列表",
                            content = @Content(schema = @Schema(implementation = Conversation.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "请求参数错误")
            }
    )
    @GetMapping
    public Result<List<Conversation>> getConversations(
            @Parameter(
                    description = "返回的对话数量限制",
                    required = false,
                    schema = @Schema(type = "integer", defaultValue = "10", example = "10")
            )
            @RequestParam(defaultValue = "10") int limit) {
        log.info("查询对话列表: limit={}", limit);
        List<Conversation> conversations = conversationService.getConversationList(limit);
        return Result.success(conversations);
    }

    /**
     * 获取对话详情（包含消息列表）
     * GET /api/conversations/{conversationId}
     */
    @Operation(
            summary = "获取对话详情",
            description = "获取指定对话的详细信息，包含所有消息记录",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功返回对话详情",
                            content = @Content(schema = @Schema(implementation = ConversationDetailResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "对话不存在")
            }
    )
    @GetMapping("/{conversationId}")
    public Result<ConversationDetailResponse> getConversationDetail(
            @Parameter(
                    description = "对话ID",
                    required = true,
                    example = "conv_123456"
            )
            @PathVariable String conversationId) {
        log.info("查询对话详情: conversationId={}", conversationId);

        Conversation conversation = conversationService.getOrCreateConversation(conversationId);
        List<Message> messages = messageMapper.selectByConversationId(conversationId);

        ConversationDetailResponse response = ConversationDetailResponse.builder()
                .conversationId(conversation.getConversationId())
                .title(conversation.getTitle())
                .userMessageCount(conversation.getUserMessageCount())
                .aiMessageCount(conversation.getAiMessageCount())
                .status(conversation.getStatus())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messages(messages)
                .build();

        return Result.success(response);
    }

    /**
     * 结束对话
     * POST /api/conversations/{conversationId}/end
     */
    @Operation(
            summary = "结束对话",
            description = "将对话状态设置为已结束，之后无法继续发送消息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "对话已成功结束"),
                    @ApiResponse(responseCode = "404", description = "对话不存在"),
                    @ApiResponse(responseCode = "400", description = "对话状态不允许结束")
            }
    )
    @PostMapping("/{conversationId}/end")
    public Result<Void> endConversation(
            @Parameter(
                    description = "对话ID",
                    required = true,
                    example = "conv_123456"
            )
            @PathVariable String conversationId) {
        log.info("结束对话: conversationId={}", conversationId);
        conversationService.endConversation(conversationId);
        return Result.success();
    }
}
