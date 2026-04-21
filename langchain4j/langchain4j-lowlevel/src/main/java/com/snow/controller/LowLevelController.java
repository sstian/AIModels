package com.snow.controller;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LowLevelController
{
    @Resource(name = "qwen")
    private ChatModel chatModelQwen;

    @Resource(name = "deepseek")
    private ChatModel chatModelDeepSeek;

    // http://localhost:9010/low/hello
    @GetMapping(value = "/low/hello")
    public String hello(@RequestParam(value = "prompt", defaultValue = "你是谁") String prompt)
    {
        String result = chatModelDeepSeek.chat(prompt);
        System.out.println("通过langchain4j调用模型返回结果："+result);
        return result;
    }

    // Token用量计算
    // http://localhost:9010/low/token
    @GetMapping(value = "/low/token")
    public String token(@RequestParam(value = "prompt", defaultValue = "你是谁") String prompt)
    {
        ChatResponse chatResponse = chatModelDeepSeek.chat(UserMessage.from(prompt));

        String result = chatResponse.aiMessage().text();
        System.out.println("通过调用大模型返回结果："+result);

        // Token 用量计算的底层api
        TokenUsage tokenUsage = chatResponse.tokenUsage();
        System.out.println("本次调用消耗的token："+tokenUsage);
        // 本次调用消耗的token：OpenAiTokenUsage { inputTokenCount = 5, inputTokensDetails = OpenAiTokenUsage.InputTokensDetails { cachedTokens = 0 }, outputTokenCount = 143, outputTokensDetails = null, totalTokenCount = 148 }

        result = result +"\t\n"+tokenUsage;
        return result;
    }
}
