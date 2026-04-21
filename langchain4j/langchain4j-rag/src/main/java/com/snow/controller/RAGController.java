package com.snow.controller;

import com.snow.service.ChatAssistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class RAGController
{
    @Resource
    InMemoryEmbeddingStore<TextSegment> embeddingStore;

    @Resource
    ChatAssistant chatAssistant;

    // http://localhost:9060/rag
    @GetMapping(value = "/rag")
    public String testAdd() throws IOException {
        //Document document = FileSystemDocumentLoader.loadDocument("alibaba-java.docx");
        ClassPathResource resource = new ClassPathResource("alibaba-java.docx");
        Document document = new ApacheTikaDocumentParser().parse(resource.getInputStream());

        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        String result = chatAssistant.chat("错误码00000和A0001分别是什么");
        System.out.println(result);

        return result;
    }
}
