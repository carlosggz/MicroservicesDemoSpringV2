package com.example.apigateway.infrastructure.config;

import com.example.apigateway.domain.AppSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final AppSettings appSettings;

    @Bean
    public WebClient moviesWebClient() {
        return WebClient.builder()
                .baseUrl(appSettings.getMoviesUrl())
                .build();
    }

    @Bean
    public WebClient actorsWebClient() {
        return WebClient.builder()
                .baseUrl(appSettings.getActorsUrl())
                .build();
    }
}
