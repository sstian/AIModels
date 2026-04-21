package com.snow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    // ChatClient 聊天对话 //
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 配置默认角色
        return builder.defaultSystem("你是尚硅谷教育的一名老师，你精通Java开发，你的名字叫尚硅谷。").build();
    }
}
