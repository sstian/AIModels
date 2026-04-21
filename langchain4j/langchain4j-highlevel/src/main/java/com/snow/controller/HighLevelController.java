package com.snow.controller;

import com.snow.service.ChatAssistant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HighLevelController
{
    @Resource
    private ChatAssistant chatAssistant;

    // http://localhost:9020/high/hello
    @GetMapping(value = "/high/hello")
    public String hello(@RequestParam(value = "prompt", defaultValue = "你是谁") String prompt)
    {
        return chatAssistant.chat(prompt);
    }
}
