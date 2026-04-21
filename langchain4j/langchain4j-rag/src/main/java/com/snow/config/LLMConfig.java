package com.snow.config;

import com.snow.service.ChatAssistant;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig
{
    @Bean
    public ChatModel chatModelDeepSeek()
    {
        return OpenAiChatModel.builder()
                        .apiKey(System.getenv("deepseek-api"))
                        .baseUrl("https://api.deepseek.com/v1")
                        .modelName("deepseek-chat")
                        //.modelName("deepseek-reasoner")
                        .build();
    }

    /**
     * 需要预处理文档并将其存储在专门的嵌入存储（也称为矢量数据库）中。当用户提出问题时，这对于快速找到相关信息是必要的。
     * 我们可以使用我们支持的 15 多个嵌入存储中的任何一个，但为了简单起见，我们将使用内存中的嵌入存储：
     * https://docs.langchain4j.dev/integrations/embedding-stores/in-memory
     */
    @Bean
    public InMemoryEmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public ChatAssistant assistant(ChatModel chatModel, EmbeddingStore<TextSegment> embeddingStore)
    {
        return AiServices.builder(ChatAssistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }
}
