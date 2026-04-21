package com.snow.controller;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ChatModelController {

    @Autowired
    // import org.springframework.ai.chat.model.ChatModel;
    private ChatModel chatModel;

    // http://localhost:8010/chatmodel/call
    @GetMapping("/chatmodel/call")
    public String chat(@RequestParam(value="msg", defaultValue="你是谁") String msg) {
        return chatModel.call(msg);
    }

    // http://localhost:8010/chatmodel/call/chatOptions
    @GetMapping("/chatmodel/call/chatOptions")
    public String chatOptions(@RequestParam(value="msg", defaultValue="你是谁") String msg) {
        ChatResponse response = chatModel.call(
                new Prompt(
                        msg,
                        OpenAiChatOptions.builder()
                                .model("deepseek-chat")
                                .temperature(0.8)
                                .build()
                )
        );
        return response.getResult().getOutput().getContent();
    }

    // 提示词
    // http://localhost:8010/chatmodel/call/prompt
    @GetMapping("/chatmodel/call/prompt")
    public String prompt(
            @RequestParam(value="name", defaultValue="Jack") String name,
            @RequestParam(value="voice", defaultValue="不要辣") String voice
    ){
        String userText= """
            给我推荐北京的至少三种美食
            """;
        UserMessage userMessage = new UserMessage(userText);

        String systemText= """
            你是一个美食咨询助手，可以帮助人们查询美食信息。
            你的名字是{name},
            你应该用你的名字和{voice}的饮食习惯回复用户的请求。
            """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));   // 替换占位符

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        List<Generation> results = chatModel.call(prompt).getResults();
        return results.stream().map(x->x.getOutput().getContent()).collect(Collectors.joining(""));
    }

}