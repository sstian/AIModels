package com.snow.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatClientController {
    @Autowired
    private ChatClient chatClient;

    // http://localhost:8010/chat
    @GetMapping("/chatchatclient/call")
    public String chat(@RequestParam(value="msg", defaultValue="你是谁") String message) {
        return chatClient.prompt().user(message).call().content();
    }

    // 流式输出
    // http://localhost:8010/chat/stream
    @GetMapping(value = "/chatclient/stream",produces="text/html;charset=UTF-8")
    public Flux<String> chatStream(@RequestParam(value="msg", defaultValue="你是谁") String message) {
        return chatClient.prompt().user(message).stream().content();
    }
}