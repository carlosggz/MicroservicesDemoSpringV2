package com.example.apigateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.securityMatcher(new NegatedServerWebExchangeMatcher(
                ServerWebExchangeMatchers.pathMatchers("/api/gateway/login", "/api/gateway/ping")));
        http.authorizeExchange().anyExchange().authenticated()
                .and()
                .oauth2ResourceServer().jwt();
        http.csrf().disable();
        return http.build();
    }
}
