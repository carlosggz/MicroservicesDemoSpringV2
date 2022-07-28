package com.example.apigateway.application;

import com.example.apigateway.domain.models.Actor;
import com.example.apigateway.domain.models.ActorDetails;
import com.example.apigateway.domain.models.Movie;
import com.example.apigateway.domain.settings.AppSettings;
import com.example.apigateway.utils.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ActorDetailHandlerTest extends BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AppSettings appSettings;

    @Autowired
    ActorDetailHandler handler;

    @ParameterizedTest
    @MethodSource("invalidParameters")
    void nullCommandThrowsException(ActorDetailsQuery query) {
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
    void whenValidActorIdItGetExpectedResult() {
        //given
        var actor = Actor.builder()
                .id("actor1")
                .firstName("actor")
                .lastName("first")
                .character("pilot1")
                .likes(2)
                .movies(Set.of("movie1", "movie2"))
                .build();
        var movies = List.of(
                Movie.builder().id("movie1").title("movie1 title").build(),
                Movie.builder().id("movie2").title("movie2 title").build()
        );
        var expectedResult = ActorDetails.builder()
                .id(actor.getId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .character(actor.getCharacter())
                .likes(actor.getLikes())
                .movies(Set.of(movies.get(0), movies.get(1)))
                .build();
        stubFor(get(urlPathMatching(appSettings.getActorDetailsPath().replace("{ACTOR-ID}", actor.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(actor))));
        stubFor(post(urlPathMatching(appSettings.getMoviesSearchPath()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(movies))));

        //when
        var result = handler.handle(new ActorDetailsQuery(actor.getId()));

        //then
        StepVerifier
                .create(result)
                .expectNext(expectedResult)
                .verifyComplete();
    }
    static Stream<Arguments> invalidParameters() {
        return Stream.of(
                Arguments.of((ActorDetailsQuery)null),
                Arguments.of(new ActorDetailsQuery(null)),
                Arguments.of(new ActorDetailsQuery("")),
                Arguments.of(new ActorDetailsQuery(" "))
        );
    }



}
