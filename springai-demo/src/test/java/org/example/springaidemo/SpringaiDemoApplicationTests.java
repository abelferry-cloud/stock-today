package org.example.springaidemo;

import org.example.springaidemo.Tools.DateTimeTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringaiDemoApplicationTests {
    @Autowired
    private  ChatClient chatClient;

    @Test
    void contextLoads() {
    }

    @Test
    void functionCallingTest(){
        String content = chatClient.prompt()
                .user("明天是几号")
                .tools(new DateTimeTools())
                .call()
                .content();

        System.out.println(content);
    }


}
