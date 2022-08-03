package org.example.actorsapi.integration;

import org.example.actorsapi.application.ActorsHandler;
import org.example.actorsapi.application.ActorsQuery;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.example.actorsapi.utils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

@SpringBootTest
class ActorsHandlerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    ActorsHandler handler;

    @Test
    void getActorsReturnsAList() {
        //given
        var dtos = Stream.of(
                        ActorsObjectMother.getRandomDbEntity(),
                        ActorsObjectMother.getRandomDbEntity(),
                        ActorsObjectMother.getRandomDbEntity()
                )
                .peek(a -> crudRepository.save(a).block())
                .map(ActorMapper.INSTANCE::toDto)
                .toList();

        //when
        var result = handler.handle(new ActorsQuery());

        //then
        StepVerifier
                .create(result)
                .expectNext(dtos.get(0))
                .expectNext(dtos.get(1))
                .expectNext(dtos.get(2))
                .verifyComplete();
    }
}
