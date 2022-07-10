package org.example.actorsapi.application;

import org.example.actorsapi.domain.actors.ActorsRepository;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActorDetailHandlerTest {

    @Mock
    ActorsRepository actorsRepository;

    @InjectMocks
    ActorDetailHandler handler;

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
    void getActorWithInvalidIdReturnsEmpty() {
        //given
        var id = "invalid id";
        when(actorsRepository.getById(id)).thenReturn(Mono.empty());

        //when
        var result = handler.handle(new ActorDetailsQuery(id));

        //then
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void getActorWithValidIdReturnsActor() {
        //given
        var actor = ActorsObjectMother.getRandomActor();
        when(actorsRepository.getById(actor.getId())).thenReturn(Mono.just(actor));

        //when
        var result = handler.handle(new ActorDetailsQuery(actor.getId()));

        //then
        StepVerifier
                .create(result)
                .expectNext(actor)
                .verifyComplete();
    }

}
