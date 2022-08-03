package org.example.actorsapi.integration;

import org.example.actorsapi.application.ActorDetailHandler;
import org.example.actorsapi.application.ActorDetailsQuery;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.example.actorsapi.utils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class ActorDetailHandlerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    ActorDetailHandler handler;

    @Test
    void getActorWithInvalidIdReturnsEmpty() {
        //given
        var id = "invalid id";

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
        crudRepository.save(ActorMapper.INSTANCE.toEntity(actor)).block();

        //when
        var result = handler.handle(new ActorDetailsQuery(actor.getId()));

        //then
        StepVerifier
                .create(result)
                .expectNext(actor)
                .verifyComplete();
    }

}
