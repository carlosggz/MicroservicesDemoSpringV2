package org.example.actorsapi.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@Data
public class RabbitProperties {
    private String host;
    private Integer port;
    private String userName;
    private String password;
    private String virtualHost;
}
