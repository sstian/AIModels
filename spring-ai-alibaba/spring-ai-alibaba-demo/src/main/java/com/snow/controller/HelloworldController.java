package com.snow.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/helloworld")
public class HelloworldController {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    private final ChatClient dashScopeChatClient;

    // 也可以使用如下的方式注入 ChatClient
    public HelloworldController(ChatClient.Builder chatClientBuilder) {

        this.dashScopeChatClient = chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .topP(0.7)
                                .build()
                )
                .build();
    }

    /**
     * ChatClient 简单调用
     *
     * http://127.0.0.1:18080/helloworld/simple/chat
     */
    @GetMapping("/simple/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query) {
        return dashScopeChatClient.prompt(query).call().content();
    }

    /**
     * ChatClient 流式调用
     *
     * http://127.0.0.1:18080/helloworld/stream/chat
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(
            HttpServletResponse response,
            @RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query) {
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatClient.prompt(query).stream().content();
    }

    /**
     * ChatClient 使用自定义的 Advisor 实现功能增强.
     * eg:
     * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=你好，我叫jack，之后的会话中都带上我的名字
     * 你好，jack！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
     * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=我叫什么名字？
     * 你叫jack呀。有什么事情想要分享或者讨论吗，jack？
     *
     * refer: https://docs.spring.io/spring-ai/reference/api/chat-memory.html#_memory_in_chat_client
     *
     * 显式指定 @PathVariable和@RequestParam 的 name 属性，为了解决以下异常：
     * java.lang.IllegalArgumentException: Name for argument of type [java.lang.String] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag.
     */
    @GetMapping("/advisor/chat/{conversationId}")
    public Flux<String> advisorChat(
            HttpServletResponse response,
            @PathVariable("conversationId") String conversationId,
            @RequestParam("query") String query
    ) {
        response.setCharacterEncoding("UTF-8");

        return this.dashScopeChatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

    /**
     * ChatClient 新的聊天接口，支持流式输出和自定义 ChatOptions 配置
     * eg:
     * http://127.0.0.1:18080/helloworld/advisor/newChat?query=你好&topP=0.8&temperature=0.9
     */
    @GetMapping("/advisor/newChat")
    public Flux<String> newChat(
            HttpServletResponse response,
            @RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query,
            @RequestParam(value = "topP", required = false) Double topP,
            @RequestParam(value = "temperature", required = false) Double temperature,
            @RequestParam(value = "maxTokens", required = false) Integer maxToken
    ) {
        response.setCharacterEncoding("UTF-8");

        // 构建 ChatOptions
        DashScopeChatOptions.DashScopeChatOptionsBuilder optionsBuilder = DashScopeChatOptions.builder();

        if (topP != null) {
            optionsBuilder.topP(topP);
        }
        if (temperature != null) {
            optionsBuilder.temperature(temperature);
        }
        if (maxToken != null) {
            optionsBuilder.maxToken(maxToken);
        }

        return this.dashScopeChatClient.prompt(query)
                .options(optionsBuilder.build())
                .stream()
                .content();
    }

}
