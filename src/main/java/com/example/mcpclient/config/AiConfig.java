package com.example.mcpclient.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.chat.base-url:https://api.openai.com}")
    String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    String apiKey;

    @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}") 
    String model;

    @Value("${ai.http.connect-timeout-ms:5000}") 
    int connectTimeoutMs;
    
    @Value("${ai.http.response-timeout-ms:60000}") 
    int responseTimeoutMs;
    
    @Value("${ai.http.read-timeout-ms:60000}") 
    int readTimeoutMs;
    
    @Value("${ai.http.write-timeout-ms:60000}") 
    int writeTimeoutMs;
    
    @Value("${ai.http.max-in-memory-size:10485760}") 
    int maxInMemorySize;

    @Bean
    public WebClient.Builder openAiWebClient(
            ) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();

        return WebClient.builder()
                //.baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeaders(headers -> headers.setBearerAuth(apiKey));
    }


    @Bean
    public OpenAiApi openAiApi(WebClient.Builder openAiWebClient) {
        return OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .webClientBuilder(openAiWebClient).build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi).build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .build();
        return ChatClient.builder(openAiChatModel)
                .defaultOptions(options)
                .defaultToolCallbacks(syncMcpToolCallbackProvider.getToolCallbacks())
                .build();
    }

    /*
    @Bean
    ChatClient chatClient(ChatModel chatModel, SyncMcpToolCallbackProvider toolCallbackProvider) {
        return ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }

     */
}