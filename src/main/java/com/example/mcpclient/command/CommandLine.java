package com.example.mcpclient.command;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommandLine {

    @Bean
    public CommandLineRunner runner(ChatClient chatClient) {
        return args -> {
            String response = chatClient.prompt("Tell me a joke").call().content();
            System.out.println(response);
        };
    }
}