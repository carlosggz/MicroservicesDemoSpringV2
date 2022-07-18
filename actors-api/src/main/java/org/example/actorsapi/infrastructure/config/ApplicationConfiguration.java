package org.example.actorsapi.infrastructure.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@EnableDiscoveryClient
public class ApplicationConfiguration {
}
