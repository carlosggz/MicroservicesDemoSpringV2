package org.example.actorsapi.application;

import org.example.actorsapi.domain.actors.ActorsRepository;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActorsHandlerTest {

    @Mock
    ActorsRepository actorsRepository;

    @InjectMocks
    ActorsHandler handler;

    @Test
    void whenNullCommandItThrowsException() {
        //when
        var result = handler.handle(null);

        //then
        StepVerifier
                .create(result)
                .expectError()
                .verify();
    }

    @Test
    void getActorsReturnsAList() {
        //given
        var entities = List.of(
                ActorsObjectMother.getRandomDto(),
                ActorsObjectMother.getRandomDto(),
                ActorsObjectMother.getRandomDto()
        );
        when(actorsRepository.getAll()).thenReturn(Flux.fromIterable(entities));

        //when
        var result = handler.handle(new ActorsQuery());

        //then
        StepVerifier
                .create(result)
                .expectNext(entities.get(0))
                .expectNext(entities.get(1))
                .expectNext(entities.get(2))
                .verifyComplete();
    }
}
