// NOT RUN!

package com.snow.controller;

import cn.hutool.core.date.DateUtil;

import com.snow.service.ChatAssistant;
import com.snow.service.ChatMemoryAssistant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ChatMemoryController
{
    @Resource(name = "chat")
    private ChatAssistant chatAssistant;

    @Resource(name = "chatMessageWindowChatMemory")
    private ChatMemoryAssistant chatMessageWindowChatMemory;

    @Resource(name = "chatTokenWindowChatMemory")
    private ChatMemoryAssistant chatTokenWindowChatMemory;

    // 没有记忆缓存功能
    @GetMapping(value = "/chatmemory/test1")
    public String chat()
    {
        String answer01 = chatAssistant.chat("你好，我的名字叫张三");
        System.out.println("answer01返回结果："+answer01);

        String answer02 = chatAssistant.chat("我的名字是什么");
        System.out.println("answer02返回结果："+answer02);

        return "success : "+ DateUtil.now()+"<br> \n\n answer01: "+answer01+"<br> \n\n answer02: "+answer02;
    }

    // MessageWindowChatMemory实现聊天功能
    @GetMapping(value = "/chatmemory/test2")
    public String chatMessageWindowChatMemory()
    {
        chatMessageWindowChatMemory.chatWithChatMemory(1L, "你好！我的名字是Java.");
        String answer01 = chatMessageWindowChatMemory.chatWithChatMemory(1L, "我的名字是什么");
        System.out.println("answer01返回结果："+answer01);

        chatMessageWindowChatMemory.chatWithChatMemory(3L, "你好！我的名字是C++");
        String answer02 = chatMessageWindowChatMemory.chatWithChatMemory(3L, "我的名字是什么");
        System.out.println("answer02返回结果："+answer02);

        return "chatMessageWindowChatMemory success : "
                + DateUtil.now()+"<br> \n\n answer01: "+answer01+"<br> \n\n answer02: "+answer02;

    }

    // TokenWindowChatMemory实现聊天功能
    @GetMapping(value = "/chatmemory/test3")
    public String chatTokenWindowChatMemory()
    {
        chatTokenWindowChatMemory.chatWithChatMemory(1L, "你好！我的名字是mysql");
        String answer01 = chatTokenWindowChatMemory.chatWithChatMemory(1L, "我的名字是什么");
        System.out.println("answer01返回结果："+answer01);

        chatTokenWindowChatMemory.chatWithChatMemory(3L, "你好！我的名字是oracle");
        String answer02 = chatTokenWindowChatMemory.chatWithChatMemory(3L, "我的名字是什么");
        System.out.println("answer02返回结果："+answer02);

        return "chatTokenWindowChatMemory success : "
                + DateUtil.now()+"<br> \n\n answer01: "+answer01+"<br> \n\n answer02: "+answer02;
    }
}
