package org.example.actorsapi.application;

import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorsRepository;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.example.actorsapi.utils.ActorsObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeHandlerTest {

    @Mock
    ActorsRepository actorsRepository;

    @InjectMocks
    LikeHandler handler;

    @Captor
    ArgumentCaptor<Actor> captor;

    @Test
    void whenNullEventThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null));
    }

    @Test
    void whenInvalidIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(LikeMovieEvent.builder()
                .aggregateRootId(null).build()));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(LikeMovieEvent.builder()
                .aggregateRootId("").build()));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(LikeMovieEvent.builder()
                .aggregateRootId("  ").build()));
    }

    @Test
    void handleWillIncrementLikes() {
        //given
        var movieId = "movieId";
        var actors = List.of(
                ActorsObjectMother.getRandomActor(),
                ActorsObjectMother.getRandomActor(),
                ActorsObjectMother.getRandomActor()
        );
        var event = LikeMovieEvent.builder()
                .aggregateRootId(movieId)
                .build();
        when(actorsRepository.getActorsInMovie(movieId)).thenReturn(Flux.fromIterable(actors));
        actors.forEach(a -> when(actorsRepository.save(a)).thenReturn(Mono.just(a)));

        //when
        handler.handle(event);

        //then
        verify(actorsRepository, times(actors.size())).save(captor.capture());
        var savedActors = captor.getAllValues();
        assertNotNull(savedActors);
        assertEquals(actors.size(), savedActors.size());

        savedActors
                .stream()
                .toList()
                .forEach(s -> {
                    assertEquals(1, s.getLikes());
                    assertTrue(actors.stream().anyMatch(a -> a.equals(s)));
                });
    }

}
