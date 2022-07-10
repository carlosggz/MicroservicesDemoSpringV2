package org.example.actorsapi.infrastructure;

import org.example.actorsapi.infrastructure.crud.ActorsCrudRepository;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
class ActorsRepositoryImplTest {

    @Autowired
    ActorsCrudRepository crudRepository;

    ActorsRepositoryImpl actorsRepository;

    @BeforeEach
    void setup() {
        crudRepository.deleteAll().block();
        actorsRepository = new ActorsRepositoryImpl(crudRepository);
    }

    @Test
    void getAllReturnsAListOfDtos() {
        //given
        var dtos = Stream.of(
                        ActorsObjectMother.getRandomDbEntity(),
                        ActorsObjectMother.getRandomDbEntity(),
                        ActorsObjectMother.getRandomDbEntity()
                )
                .peek(x -> crudRepository.save(x).block())
                .map(ActorMapper.INSTANCE::toDto)
                .toList();

        //when
        var result = actorsRepository.getAll();

        //then
        StepVerifier
                .create(result)
                .expectNext(dtos.get(0))
                .expectNext(dtos.get(1))
                .expectNext(dtos.get(2))
                .verifyComplete();
    }

    @Test
    void getByIdWithInvalidIdReturnsEmpty() {
        //when
        var result = actorsRepository.getById("invalid-id");

        //then
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void getByIdWithValidIdReturnsTheActor() {
        //given
        var domainActor = ActorsObjectMother.getRandomActor();
        crudRepository.save(ActorMapper.INSTANCE.toEntity(domainActor)).block();

        //when
        var result = actorsRepository.getById(domainActor.getId());

        //then
        StepVerifier
                .create(result)
                .expectNext(domainActor)
                .verifyComplete();
    }

    @Test
    void saveWithNewActorAddsActor() {
        //given
        var domainActor = ActorsObjectMother.getRandomActor();

        //when
        var result = actorsRepository.save(domainActor);

        //then
        StepVerifier
                .create(result)
                .expectNext(domainActor)
                .verifyComplete();

        var dbActors = crudRepository
                .findAll()
                .map(ActorMapper.INSTANCE::toDomain)
                .collectList()
                        .block();

        assertNotNull(dbActors);
        assertEquals(1, dbActors.size());
        assertEquals(domainActor, dbActors.get(0));
    }

    @Test
    void saveWithExistingActorUpdatesActor() {
        //given
        var domainActor = ActorsObjectMother.getRandomActor();
        crudRepository.save(ActorMapper.INSTANCE.toEntity(domainActor)).block();
        domainActor.setLikes(123);

        //when
        var result = actorsRepository.save(domainActor);

        //then
        StepVerifier
                .create(result)
                .expectNext(domainActor)
                .verifyComplete();

        var dbActors = crudRepository
                .findAll()
                .map(ActorMapper.INSTANCE::toDomain)
                .collectList()
                .block();

        assertNotNull(dbActors);
        assertEquals(1, dbActors.size());
        assertEquals(domainActor, dbActors.get(0));
    }

}
