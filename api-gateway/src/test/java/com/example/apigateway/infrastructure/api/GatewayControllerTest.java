package com.example.apigateway.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import com.example.apigateway.application.ActorDetailsQuery;
import com.example.apigateway.domain.ActorDetails;
import com.example.apigateway.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = GatewayController.class)
class GatewayControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    Pipeline pipeline;

    @Test
    void getActorDetailsReturnsTheAggregation() {
        //given
        var expectedResult = ActorDetails.builder()
                .id("actor1")
                .firstName("actor")
                .lastName("first")
                .character("pilot1")
                .likes(2)
                .movies(Set.of(Movie.builder().id("movie1").title("movie1 title").build()))
                .build();
        when(pipeline.send(any(ActorDetailsQuery.class))).thenReturn(Mono.just(expectedResult));

        //when/then
        var result = webClient
                .get()
                .uri("/api/gateway/actor/" + expectedResult.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActorDetails.class)
                .returnResult();

        var returnedActor = result.getResponseBody();
        assertEquals(expectedResult, returnedActor);

    }

}
