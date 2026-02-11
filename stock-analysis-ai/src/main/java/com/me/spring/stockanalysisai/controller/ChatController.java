package com.me.spring.stockanalysisai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.apache.coyote.http11.Constants.a;

@RestController
@Tag(name = "ChatController", description = "聊天接口")
public class ChatController {

    @Autowired
    private final ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    @Operation(summary = "聊天接口", description = "接收用户输入，返回AI回复")
    public String chat(String userPrompt, String conversationId) {

        return chatClient.prompt()
                .user(userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @GetMapping("chat-stream")
    @Operation(summary = "聊天接口(流式输出)", description = "接收用户输入，返回AI回复")
    public Flux<String> chatStream(String userPrompt, String conversationId) {
        return chatClient.prompt()
                .user(userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

}
