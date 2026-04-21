package com.snow.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatMemoryAssistant {
    String chatWithChatMemory(@MemoryId Long userId, @UserMessage String prompt);
}
