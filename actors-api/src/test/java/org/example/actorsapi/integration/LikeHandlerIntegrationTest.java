package org.example.actorsapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import lombok.SneakyThrows;
import org.example.actorsapi.application.LikeHandler;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.example.actorsapi.infrastructure.config.MessagingProperties;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.example.actorsapi.utils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LikeHandlerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    MessagingProperties messagingProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Sender sender;

    @Autowired
    LikeHandler handler;

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

        //when
        sendMessage(movieId);
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

    @SneakyThrows
    private void sendMessage(String movieId) {
        var event = LikeMovieEvent.builder()
                .aggregateRootId(movieId)
                .build();
        var properties = new AMQP.BasicProperties.Builder()
                .headers(Map.of())
                .build();
        var body = objectMapper.writeValueAsBytes(event);
        var message = new OutboundMessage(
                messagingProperties.getExchangeName(), messagingProperties.getRoutingKey(), properties, body);

        Mono
                .just(message)
                .transform(sender::send)
                .then()
                .block();
    }

}
