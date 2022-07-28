package com.example.apigateway.infrastructure.components;

import com.example.apigateway.domain.models.Actor;
import com.example.apigateway.domain.settings.AppSettings;
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

import java.util.Set;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ActorComponentImplTest extends BaseIntegrationTest  {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AppSettings appSettings;
    @Autowired
    ActorComponentImpl component;

    @ParameterizedTest
    @MethodSource("invalidIds")
    void invalidIdThrowsError(String id) {
        //when
        var result = component.getActor(id);

        //then
        StepVerifier
                .create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

    }

    @Test
    void whenActorIsNotValidReturnsEmpty() {
        //given
        var id = "123";
        stubFor(get(urlPathMatching(appSettings.getActorDetailsPath().replace("{ACTOR-ID}", id)))
                .willReturn(aResponse()
                        .withStatus(404)));

        //when
        var result = component.getActor(id);

        //then
        StepVerifier
                .create(result)
                .expectError(WebClientResponseException.NotFound.class)
                .verify();
    }

    @Test
    @SneakyThrows
    void whenActorIsValidReturnsDetails() {
        //given
        var actor = Actor.builder()
                .id("actor1")
                .firstName("actor")
                .lastName("first")
                .character("pilot1")
                .likes(2)
                .movies(Set.of("movie1", "movie2"))
                .build();
        stubFor(get(urlPathMatching(appSettings.getActorDetailsPath().replace("{ACTOR-ID}", actor.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(actor))));

        //when
        var result = component.getActor(actor.getId());

        //then
        StepVerifier
                .create(result)
                .expectNext(actor)
                .verifyComplete();
    }

    static Stream<Arguments> invalidIds() {
        return Stream.of(
                Arguments.of((String)null),
                Arguments.of(""),
                Arguments.of(" ")
        );
    }

}
