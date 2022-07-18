package com.example.apigateway.infrastructure.config;

import com.example.apigateway.domain.AppSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableDiscoveryClient
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "test", havingValue = "false", matchIfMissing = true)
public class ApplicationConfiguration {

    private final AppSettings appSettings;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Bean
    @LoadBalanced
    public WebClient moviesWebClient() {
        return WebClient.builder()
                .filter(lbFunction)
                .baseUrl("http://" + appSettings.getMoviesService())
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient actorsWebClient() {
        return WebClient.builder()
                .filter(lbFunction)
                .baseUrl("http://" + appSettings.getActorsService())
                .build();
    }
}
