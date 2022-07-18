package com.example.apigateway.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebclientsTestConfiguration {

    @Bean
    public WebClient moviesWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8888")
                .build();
    }

    @Bean
    public WebClient actorsWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8888")
                .build();
    }
}
