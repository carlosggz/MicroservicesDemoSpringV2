package com.example.apigateway.domain.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.auth")
@Getter
@Setter
@NoArgsConstructor
public class AuthSettings {

    String clientId;
    String clientSecret;
    String authUrl;
    String grantType;
    String scope;
    String realm;
}
