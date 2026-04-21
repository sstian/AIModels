package com.snow.config;

import com.snow.service.ChatAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig
{
    @Bean(name = "qwen")
    public ChatModel chatModelQwen()
    {
        return OpenAiChatModel.builder()
                    .apiKey(System.getenv("aliQwen-api"))
                    .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                    .modelName("qwen-plus")
                    .build();
    }

    @Bean(name = "deepseek")
    public ChatModel chatModelDeepSeek()
    {
        return OpenAiChatModel.builder()
                        .apiKey(System.getenv("deepseek-api"))
                        .baseUrl("https://api.deepseek.com/v1")
                        .modelName("deepseek-chat")
                        //.modelName("deepseek-reasoner")
                        .build();
    }

    // high level api
    @Bean
    public ChatAssistant chatAssistant(@Qualifier("deepseek") ChatModel chatModelDeepSeek)
    {
        return AiServices.create(ChatAssistant.class, chatModelDeepSeek);
    }
}
