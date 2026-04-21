package com.snow.service;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.stereotype.Service;

@AiService
public interface ChatAssistant
{
    String chat(String prompt);
}
