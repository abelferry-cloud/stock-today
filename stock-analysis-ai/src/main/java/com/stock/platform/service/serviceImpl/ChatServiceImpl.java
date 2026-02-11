package com.stock.platform.service.serviceImpl;

import com.stock.platform.pojo.dto.request.ChatRequest;
import com.stock.platform.pojo.dto.response.ChatResponse;
import com.stock.platform.service.ChatService;
import com.stock.platform.service.ConversationService;
import com.stock.platform.service.RagRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * 聊天服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ConversationService conversationService;
    private final RagRetrievalService ragRetrievalService;

    /**
     * 股票分析专家 System 提示词
     */
    private static final String SYSTEM_PROMPT =
            "你是一位专业的股票分析专家，擅长分析股票市场动态、" +
                    "公司财报和投资建议。请用专业、客观的语气回答用户问题。\n\n" +
                    "当用户提供相关资料时，请基于这些资料进行分析。如果资料不足，" +
                    "可以基于你的专业知识进行补充分析，但请明确指出哪些是基于资料的，" +
                    "哪些是你的专业判断。\n\n" +
                    "重要提示：\n" +
                    "1. 投资有风险，请在回答的最后加上风险提示\n" +
                    "2. 如果提供的资料与你的知识库有冲突，请以提供的资料为准，并说明差异\n" +
                    "3. 对于具体数据，请引用资料中的数据来源";

    @Override
    public ChatResponse chat(ChatRequest request) {
        // 生成或获取会话 ID
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        }

        log.info("收到聊天请求: conversationId={}, message={}",
                conversationId, request.getMessage());

        // 保存用户消息到数据库
        conversationService.saveUserMessage(conversationId, request.getMessage());

        // RAG检索：判断是否需要向量检索
        String ragContext = "";
        if (ragRetrievalService.requiresRagRetrieval(request.getMessage())) {
            // 尝试从用户问题中提取股票代码
            String stockCode = ragRetrievalService.extractStockCode(request.getMessage());
            // 检索相关文档
            ragContext = ragRetrievalService.retrieveRelevantContext(request.getMessage(), stockCode);

            if (!ragContext.isEmpty()) {
                log.info("RAG检索成功: contextLength={}, stockCode={}",
                        ragContext.length(), stockCode);
            } else {
                log.info("RAG检索未找到相关文档: query={}", request.getMessage());
            }
        }

        // 创建会话记忆 Advisor
        MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(conversationId)
                .build();

        // 构建用户消息（如果有RAG上下文，则附加到消息中）
        String userMessage = request.getMessage();
        if (!ragContext.isEmpty()) {
            userMessage = "【参考资料】\n" + ragContext + "\n\n" +
                         "【用户问题】\n" + request.getMessage();
            log.debug("增强后的用户消息: length={}", userMessage.length());
        }

        // 调用 AI 模型（普通对话）
        String content = chatClient.prompt()
                .system(SYSTEM_PROMPT)  // System 设定
                .user(userMessage)  // 用户消息（可能包含RAG上下文）
                .advisors(advisor)  // 添加会话记忆
                .call()  // call 方法 - 非流式
                .content();

        assert content != null;

        // 保存AI回复到数据库
        conversationService.saveAssistantMessage(conversationId, content);

        log.info("AI 回复成功: contentLength={}, conversationId={}",
                content.length(), conversationId);

        return ChatResponse.builder()
                .content(content)
                .conversationId(conversationId)
                .build();
    }

    @Override
    public Flux<String> chatStream(ChatRequest request) {
        // 生成或获取会话 ID（final 变量）
        final String conversationId;
        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        } else {
            conversationId = request.getConversationId();
        }

        log.info("收到流式聊天请求: conversationId={}, message={}",
                conversationId, request.getMessage());

        // 保存用户消息到数据库
        conversationService.saveUserMessage(conversationId, request.getMessage());

        // RAG检索：判断是否需要向量检索
        String ragContext = "";
        if (ragRetrievalService.requiresRagRetrieval(request.getMessage())) {
            // 尝试从用户问题中提取股票代码
            String stockCode = ragRetrievalService.extractStockCode(request.getMessage());
            // 检索相关文档
            ragContext = ragRetrievalService.retrieveRelevantContext(request.getMessage(), stockCode);

            if (!ragContext.isEmpty()) {
                log.info("RAG检索成功: contextLength={}, stockCode={}",
                        ragContext.length(), stockCode);
            } else {
                log.info("RAG检索未找到相关文档: query={}", request.getMessage());
            }
        }

        // 创建会话记忆 Advisor
        MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(conversationId)
                .build();

        // 构建用户消息（如果有RAG上下文，则附加到消息中）
        String userMessage = request.getMessage();
        if (!ragContext.isEmpty()) {
            userMessage = "【参考资料】\n" + ragContext + "\n\n" +
                         "【用户问题】\n" + request.getMessage();
            log.debug("增强后的用户消息: length={}", userMessage.length());
        }

        // 用于累积完整响应内容
        StringBuilder fullContent = new StringBuilder();

        // 流式响应
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)  // 用户消息（可能包含RAG上下文）
                .advisors(advisor)  // 添加会话记忆
                .stream()  // stream 方法 - 流式
                .content()
                .doOnNext(chunk -> {
                    // 累积内容
                    fullContent.append(chunk);
                    log.debug("流式输出片段[{}]: {}...",
                            conversationId, truncate(chunk));
                })
                .doOnComplete(() -> {
                    // 流式输出完成，保存完整回复到数据库
                    String completeContent = fullContent.toString();
                    conversationService.saveAssistantMessage(conversationId, completeContent);
                    log.info("流式输出完成: conversationId={}, contentLength={}",
                            conversationId, completeContent.length());
                })
                .doOnError(error -> log.error("流式输出错误: conversationId={}", conversationId, error));
    }

    /**
     * 截断日志（避免日志过长）
     */
    private String truncate(String str) {
        if (str == null) {
            return null;
        }
        return str.length() > 50 ? str.substring(0, 50) + "..." : str;
    }
}
