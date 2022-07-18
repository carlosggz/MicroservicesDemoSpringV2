package com.example.apigateway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@NoArgsConstructor
public class AppSettings {
    private String moviesUrl;
    private String actorsUrl;
    private String moviesSearchPath;
    private String actorDetailsPath;
    private long timeoutSeconds;
    private long backoffSeconds;
    private long backoffRetries;
}
