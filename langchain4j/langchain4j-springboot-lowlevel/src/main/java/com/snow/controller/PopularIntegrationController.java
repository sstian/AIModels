package com.snow.controller;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PopularIntegrationController
{
    @Resource
    private ChatModel chatModel;

    // http://localhost:9030/low/boot/hello
    @GetMapping(value = "/low/boot/hello")
    public String hello(@RequestParam(value = "prompt", defaultValue = "你是谁") String prompt)
    {
        return chatModel.chat(prompt);
    }
}
