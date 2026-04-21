package com.snow.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    // 依赖注入
    // 方式1. @Autowired, but Field injection is not recommended
//    @Autowired
//    private OpenAiChatModel chatModel;

    // 方式2. constructor
    private final OpenAiChatModel chatModel;
    public DemoController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }


    // http://localhost:8010/hello
    @GetMapping("/hello")
    public String hello(@RequestParam(value="msg", defaultValue="hello") String message) {
        String result = chatModel.call(message);
        System.out.println(result);
        return result;
    }
}
