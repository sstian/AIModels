// > NOT RUN!

package com.snow.controller;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllamaController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @GetMapping("/ollama")
    public String generate(@RequestParam(value = "msg", defaultValue = "hello") String message) {
        String response = ollamaChatModel.call(message);
        System.out.println(response);
        return response;
    }
}