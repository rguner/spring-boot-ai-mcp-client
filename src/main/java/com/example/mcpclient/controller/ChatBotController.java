package com.example.mcpclient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatClient chatClient;

    @GetMapping("chat")
    public String chat(@RequestBody String question) {

        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();

    }

}