package com.example.apigateway.infrastructure.components;

import com.example.apigateway.domain.AppSettings;
import com.example.apigateway.domain.Movie;
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
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class MovieComponentImplTest extends BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AppSettings appSettings;

    @Autowired
    MovieComponentImpl component;

    @Test
    void invalidListThrowsError() {
        //when
        var result = component.getMovies(null);

        //then
        StepVerifier
                .create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

    }

    @ParameterizedTest
    @MethodSource("invalidList")
    void invalidListReturnsEmpty(List<String> ids) {
        //when
        var result = component.getMovies(ids);

        //then
        StepVerifier
                .create(result)
                .verifyComplete();

    }

    @Test
    @SneakyThrows
    void whenNoMatchingReturnsAnEmptyList() {
        ///given
        setExpectedValue(List.of());

        //when
        var result = component.getMovies(List.of("123"));

        //then
        StepVerifier
                .create(result)
                .verifyComplete();

    }

    @Test
    @SneakyThrows
    void whenMatchingReturnList() {
        //given
        var movies = List.of(
                Movie.builder().id("movie1").title("movie1 title").build(),
                Movie.builder().id("movie2").title("movie2 title").build()
        );
        setExpectedValue(movies);

        //when
        var result = component.getMovies(movies.stream().map(Movie::getId).toList());

        //then
        StepVerifier
                .create(result)
                .expectNext(movies.get(0))
                .expectNext(movies.get(1))
                .verifyComplete();
    }

    static Stream<Arguments> invalidList() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of("")),
                Arguments.of(List.of(" ")),
                Arguments.of(List.of("", " ", "    "))
        );
    }

    @SneakyThrows
    private void setExpectedValue(List<Movie> movies) {
        stubFor(post(urlPathMatching(appSettings.getMoviesSearchPath()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(movies))));
    }
}
