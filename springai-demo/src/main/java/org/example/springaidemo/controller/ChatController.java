package org.example.springaidemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Tag(name = "ChatController", description = "聊天接口")
public class ChatController {

    @Autowired
    private final ChatClient chatClient;

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
