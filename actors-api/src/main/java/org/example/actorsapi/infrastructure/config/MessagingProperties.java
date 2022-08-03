package org.example.actorsapi.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.messaging")
@Data
public class MessagingProperties {
    private String exchangeName;
    private String exchangeType;
    private String routingKey;
    private String queueName;
}
