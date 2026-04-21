package com.snow.config;

import com.snow.listener.MyChatModelListener;
import com.snow.service.*;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

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

                    // 模型参数
                    // 日志。日志级别设置为debug才有效
                    .logRequests(true)
                    .logResponses(true)
                    // 监听
                    .listeners(List.of(new MyChatModelListener()))
                    // 重试
                    .maxRetries(2)
                    // 超时。向大模型发送请求时，如在指定时间内没有收到响应，该请求将被中断并报request timed out
                    .timeout(Duration.ofSeconds(60))

                    .build();
    }


    // 流式输出 //
    @Bean
    public StreamingChatModel streamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                    .apiKey(System.getenv("deepseek-api"))
                    .baseUrl("https://api.deepseek.com/v1")
                    .modelName("deepseek-chat")
                    .build();
    }

    @Bean(name = "chat")
    public ChatAssistant chatAssistant(StreamingChatModel streamingChatModel){
        return AiServices.create(ChatAssistant.class, streamingChatModel);
    }


    // 视觉理解 //
    // 使用通义千问实现图像理解
    @Bean(name = "qwen-vl")
    public ChatModel imageModel() {
        return OpenAiChatModel.builder()
                    .apiKey(System.getenv("aliQwen-api"))
                    .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                    // wen-vl-max 是一个多模态大模型，支持图片和文本的结合输入，适用于视觉-语言任务
                    .modelName("qwen-vl-max")

                    .build();
    }

    // 使用通义万象实现文生图
    // 图片生成 https://help.aliyun.com/zh/model-studio/text-to-image
    @Bean
    public WanxImageModel wanxImageModel()
    {
        return WanxImageModel.builder()
                    .apiKey(System.getenv("aliQwen-api"))
                    .modelName("wanx2.1-t2i-turbo")
                    .build();
    }


    // 聊天记忆 //
    @Bean(name = "chatMessageWindowChatMemory")
    public ChatMemoryAssistant chatMessageWindowChatMemory(@Qualifier("deepseek") ChatModel chatModel)
    {
        return AiServices.builder(ChatMemoryAssistant.class)
                    .chatModel(chatModel)
                    //按照memoryId对应创建了一个chatMemory
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(100))
                    .build();
    }

    @Bean(name = "chatTokenWindowChatMemory")
    public ChatMemoryAssistant chatTokenWindowChatMemory(@Qualifier("deepseek") ChatModel chatModel)
    {
        // TokenCountEstimator默认的token分词器，需要结合Tokenizer计算ChatMessage的token数量
        TokenCountEstimator openAiTokenCountEstimator = new OpenAiTokenCountEstimator("gpt-4");

        return AiServices.builder(ChatMemoryAssistant.class)
                    .chatModel(chatModel)
                    .chatMemoryProvider(memoryId -> TokenWindowChatMemory.withMaxTokens(1000,openAiTokenCountEstimator))
                    .build();
    }


    // 提示词工程 //
    @Bean
    public LawPromptAssistant lawAssistant(@Qualifier("deepseek") ChatModel chatModel) {
        return AiServices.create(LawPromptAssistant.class, chatModel);
    }


    // redis 持久化 //
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean
    public ChatPersistenceAssistant chatMemoryAssistant(@Qualifier("deepseek") ChatModel chatModel)
    {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(1000)
                .chatMemoryStore(redisChatMemoryStore)
                .build();

        return AiServices.builder(ChatPersistenceAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }


    // Function Calling //
    // 第一组 Low Level Tool API
    // https://docs.langchain4j.dev/tutorials/tools#low-level-tool-api
    /*@Bean
    public FunctionAssistant functionAssistant(ChatModel chatModel)
    {
        // 工具说明 ToolSpecification
        ToolSpecification toolSpecification = ToolSpecification.builder()
                    .name("开具发票助手")
                    .description("根据用户提交的开票信息，开具发票")
                    .parameters(JsonObjectSchema.builder()
                                .addStringProperty("companyName", "公司名称")
                                .addStringProperty("dutyNumber", "税号序列")
                                .addStringProperty("amount", "开票金额，保留两位有效数字")
                            .build())
                .build();

        // 业务逻辑 ToolExecutor
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            System.out.println(toolExecutionRequest.id());
            System.out.println(toolExecutionRequest.name());
            String arguments1 = toolExecutionRequest.arguments();
            System.out.println("arguments1****》 " + arguments1);
            return "开具成功";
        };

        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor)) // Tools (Function Calling)
                .build();
    }*/

    // 第二组 High Level Tool API
    // https://docs.langchain4j.dev/tutorials/tools#high-level-tool-api
    @Bean
    public FunctionAssistant functionAssistant(@Qualifier("deepseek") ChatModel chatModel)
    {
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(new InvoiceHandler())
                .build();
    }


    // 向量化存储 //
    // 创建向量模型
    @Bean
    public EmbeddingModel embeddingModel()
    {
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("aliQwen-api"))
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("text-embedding-v3")
                .build();
    }

    // 创建Qdrant客户端
    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder grpcClientBuilder =
                QdrantGrpcClient.newBuilder("127.0.0.1", 6334, false);
        return new QdrantClient(grpcClientBuilder.build());
    }

    // 创建向量存储
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6334)
                .collectionName("test-qdrant")
                .build();
    }
}
