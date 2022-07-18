package com.example.apigateway.utils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
@Import(WebclientsTestConfiguration.class)
public abstract class BaseIntegrationTest {
}
