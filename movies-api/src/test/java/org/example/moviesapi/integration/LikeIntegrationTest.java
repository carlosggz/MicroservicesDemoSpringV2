package org.example.moviesapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.example.moviesapi.application.LikeCommand;
import org.example.moviesapi.application.LikeHandler;
import org.example.moviesapi.domain.events.LikeMovieEvent;
import org.example.moviesapi.infrastructure.events.EventsPublisherImpl;
import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.cloud.stream.bindings.like-out-0.destination=test-topic")
@Import(TestChannelBinderConfiguration.class)
public class LikeIntegrationTest {

    static final String TOPIC_NAME = "test-topic";
    @Autowired
    private MoviesCrudRepository crudRepository;

    @Autowired
    private LikeHandler handler;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        crudRepository.deleteAll();
        outputDestination.clear(TOPIC_NAME);
    }

    @Test
    void likeWithInvalidCodeReturnsFalse() {
        //when
        var result = handler.handle(new LikeCommand("invalid-id"));

        //then
        assertFalse(result);
    }

    @Test
    void getDetailsWithValidCodeReturnsMovie() {
        //given
        var dbEntity = MoviesObjectMother.getRandomDbEntity();
        var domainEntity = MovieMapper.INSTANCE.toDomain(dbEntity);
        crudRepository.save(dbEntity);

        //when
        var result = handler.handle(new LikeCommand(domainEntity.getId()));

        //then
        assertTrue(result);

        var dbMovies = crudRepository.findAll();
        assertEquals(1, dbMovies.size());

        assertEquals(domainEntity.getLikes()+1, dbMovies.get(0).getLikes());

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    val message = outputDestination.receive(1000, TOPIC_NAME);
                    assertNotNull(message);
                    assertEquals(LikeMovieEvent.EVENT_NAME, message.getHeaders().get(EventsPublisherImpl.ROUTING_KEY));
                    val event = objectMapper.readValue(message.getPayload(), LikeMovieEvent.class);
                    assertNotNull(event);
                    assertEquals(1, event.getLikes());
                    assertEquals(domainEntity.getId(), event.getAggregateRootId());
                    assertNotNull(event.getOccurrenceDate());
        });
    }

}
