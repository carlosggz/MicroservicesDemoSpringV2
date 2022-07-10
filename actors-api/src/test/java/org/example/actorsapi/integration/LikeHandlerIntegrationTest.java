package org.example.actorsapi.integration;

import lombok.SneakyThrows;
import org.example.actorsapi.application.LikeHandler;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.example.actorsapi.infrastructure.crud.ActorsCrudRepository;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.cloud.stream.bindings.likeListener-in-0.destination=test-topic")
@Import(TestChannelBinderConfiguration.class)
class LikeHandlerIntegrationTest {
    public static final String ROUTING_HEADER = "myRoutingKey";
    public static final String ROUTING_KEY = "movie.like";
    public static final String TOPIC_NAME = "test-topic";
    @Autowired
    ActorsCrudRepository crudRepository;

    @Autowired
    LikeHandler handler;

    @Autowired
    InputDestination inputDestination;

    @Test
    @SneakyThrows
    void handleWillIncrementLikes() {
        //given
        var movieId = "movieId";
        var actors = Stream.of(
                        ActorsObjectMother.getRandomActor(),
                        ActorsObjectMother.getRandomActor(),
                        ActorsObjectMother.getRandomActor()
                )
                .peek(x -> x.setMovies(Set.of(movieId)))
                .peek(x -> crudRepository.save(ActorMapper.INSTANCE.toEntity(x)).block())
                .toList();
        var event = LikeMovieEvent.builder()
                .aggregateRootId(movieId)
                .build();
        var message = MessageBuilder
                .withPayload(event)
                .setHeader(ROUTING_HEADER, ROUTING_KEY)
                .build();

        //when
        inputDestination.send(message, TOPIC_NAME);
        Thread.sleep(500);

        //then
        var savedActors = crudRepository.findAll().collectList().block();
        assertNotNull(savedActors);
        assertEquals(actors.size(), savedActors.size());

        savedActors
                .stream()
                .map(ActorMapper.INSTANCE::toDomain)
                .forEach(s -> {
                    assertEquals(1, s.getLikes());
                    s.setLikes(0);
                    assertTrue(actors.stream().anyMatch(a -> a.equals(s)));
                });
    }

}
