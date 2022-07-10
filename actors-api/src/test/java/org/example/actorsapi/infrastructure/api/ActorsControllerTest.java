package org.example.actorsapi.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import org.example.actorsapi.application.ActorDetailsQuery;
import org.example.actorsapi.application.ActorsQuery;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ActorsController.class)
class ActorsControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    Pipeline pipeline;

    @Captor
    ArgumentCaptor<ActorDetailsQuery> detailsCaptor;

    @Test
    void getActorsReturnsAListOfActors() {
        //given
        var actors = List.of(
                ActorsObjectMother.getRandomDto(),
                ActorsObjectMother.getRandomDto(),
                ActorsObjectMother.getRandomDto()
        );
        when(pipeline.send(any(ActorsQuery.class))).thenReturn(Flux.fromIterable(actors));

        //when/then
        var result = webClient
                .get()
                .uri("/api/v1/actors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ActorDto.class)
                .returnResult();

        var returnedActors = result.getResponseBody();
        assertNotNull(returnedActors);
        assertEquals(actors.size(), returnedActors.size());

        returnedActors.forEach(r -> assertTrue(actors.stream().anyMatch(a -> a.equals(r))));
    }

    @Test
    void getActorDetailsWithInvalidActorReturns404() {
        //given
        var id = "invalid-id";
        when(pipeline.send(any(ActorDetailsQuery.class))).thenReturn(Mono.empty());

        //when/then
        webClient
                .get()
                .uri("/api/v1/actors/" + id)
                .exchange()
                .expectStatus().isNotFound();

        verify(pipeline).send(detailsCaptor.capture());
        assertEquals(id, detailsCaptor.getValue().getId());
    }

    @Test
    void getActorDetailsWithValidActorReturnsActor() {
        //given
        var actor = ActorsObjectMother.getRandomActor();
        when(pipeline.send(any(ActorDetailsQuery.class))).thenReturn(Mono.just(actor));

        //when/then
        var result = webClient
                .get()
                .uri("/api/v1/actors/" + actor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Actor.class)
                .returnResult();

        assertEquals(actor, result.getResponseBody());

        verify(pipeline).send(detailsCaptor.capture());
        assertEquals(actor.getId(), detailsCaptor.getValue().getId());
    }

}
