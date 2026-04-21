package com.snow.controller;

import com.snow.service.ChatAssistant;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class StreamingChatModelController {
    // 方式1. 直接使用 low level api
    @Resource
    private StreamingChatModel streamingChatModel;

    // 方式2. 自己封装接口使用 high level api
    @Resource
    private ChatAssistant chatAssistant;

    // 流式输出
    // http://localhost:9050/low/streamchat
    @GetMapping(value = "/low/streamchat")
    public Flux<String> lowStreamChat(@RequestParam(value = "prompt", defaultValue = "北京有什么好吃的") String prompt)
    {
        System.out.println("---come in lowStreamChat");

        return Flux.create(emitter -> {
            streamingChatModel.chat(prompt, new StreamingChatResponseHandler()
            {
                @Override
                public void onPartialResponse(String partialResponse)
                {
                    emitter.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse)
                {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable throwable)
                {
                    emitter.error(throwable);
                }
            });
        });
    }

    // http://localhost:9050/high/streamchat
    @GetMapping(value = "/high/streamchat")
    public Flux<String> highStreamChat(@RequestParam(value = "prompt", defaultValue = "南京有什么好吃") String prompt)
    {
        System.out.println("---come in highStreamChat");

        return chatAssistant.chatFlux(prompt);
    }
}

