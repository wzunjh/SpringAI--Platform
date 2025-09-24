package com.example.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {
    @Bean
    public ChatClient chatClient(OllamaChatModel model){

        return ChatClient.builder(model).defaultSystem("你是智能助手 家辉").build();
    }

}
