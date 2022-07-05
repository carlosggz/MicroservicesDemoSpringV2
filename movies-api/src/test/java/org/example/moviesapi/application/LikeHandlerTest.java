package org.example.moviesapi.application;

import org.example.moviesapi.domain.events.EventsPublisher;
import org.example.moviesapi.domain.events.LikeMovieEvent;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeHandlerTest {

    @Mock
    MoviesRepository repository;

    @Mock
    EventsPublisher eventsPublisher;

    @InjectMocks
    LikeHandler handler;

    @Captor
    ArgumentCaptor<LikeMovieEvent> captor;

    @Test
    void whenNullCommandItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null));
    }

    @Test
    void whenInvalidIdItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new LikeCommand(null)));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new LikeCommand("")));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new LikeCommand(" ")));
    }

    @Test
    void whenInvalidMovieCodeItReturnsFalse() {
        //given
        var id = "invalid";
        var query = new LikeCommand(id);
        when(repository.getById(id)).thenReturn(Optional.empty());

        //when
        var result = handler.handle(query);

        //then
        assertFalse(result);
        verifyNoInteractions(eventsPublisher);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void whenValidMovieCodeItIncrementsLikesAndRaiseAnEvent() {
        //given
        var entity = MoviesObjectMother.getRandomMovie();
        var query = new LikeCommand(entity.getId());
        when(repository.getById(entity.getId())).thenReturn(Optional.of(entity));

        //when
        var result = handler.handle(query);

        //then
        assertTrue(result);

        verify(repository).save(entity);
        assertEquals(1, entity.getLikes());

        verify(eventsPublisher).publish(captor.capture());
        var event = captor.getValue();
        assertNotNull(event);
        assertEquals(entity.getId(), event.getAggregateRootId());
        assertEquals(1, event.getLikes());
        assertNotNull(event.getOccurrenceDate());
    }
}
