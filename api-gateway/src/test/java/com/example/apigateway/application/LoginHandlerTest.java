package com.example.apigateway.application;

import com.example.apigateway.domain.dtos.LoginRequestDto;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import com.example.apigateway.domain.settings.AuthSettings;
import com.example.apigateway.utils.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class LoginHandlerTest extends BaseIntegrationTest {

    @Autowired
    AuthSettings authSettings;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginHandler handler;

    @ParameterizedTest
    @MethodSource("invalidParameters")
    void nullCommandThrowsException(LoginQuery query) {
        //when
        var result = handler.handle(query);

        //then
        StepVerifier
                .create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @SneakyThrows
    void whenValidCredentialsReturnsTheInformation(){
        //given
        var credentials = new LoginRequestDto("user", "password");
        var expectedResult = LoginResponseDto.builder()
                .accessToken("access-token")
                .expiresIn(123)
                .refreshExpiresIn(456)
                .refreshToken("refresh-token")
                .scope("scope")
                .tokenType("token-type")
                .build();
        stubFor(post(urlPathMatching("/realms/" + authSettings.getRealm() + "/protocol/openid-connect/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(expectedResult))));

        //when
        var result = handler.handle(new LoginQuery(credentials));

        //then
        StepVerifier
                .create(result)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void whenInvalidCredentialsReturnsUnauthorized(){
        //given
        var credentials = new LoginRequestDto("user", "password");
        stubFor(post(urlPathMatching("/realms/" + authSettings.getRealm() + "/protocol/openid-connect/token"))
                .willReturn(aResponse()
                        .withStatus(401)));

        //when
        var result = handler.handle(new LoginQuery(credentials));

        //then
        StepVerifier
                .create(result)
                .expectError(WebClientResponseException.Unauthorized.class)
                .verify();
    }

    static Stream<Arguments> invalidParameters() {
        return Stream.of(
                Arguments.of((LoginQuery)null),
                Arguments.of(new LoginQuery(null)),
                Arguments.of(new LoginQuery(new LoginRequestDto(null, null))),
                Arguments.of(new LoginQuery(new LoginRequestDto("", ""))),
                Arguments.of(new LoginQuery(new LoginRequestDto(" ", " ")))
        );
    }
}
