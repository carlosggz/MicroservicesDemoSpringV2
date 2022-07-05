package org.example.moviesapi.application;

import org.example.moviesapi.domain.movies.MoviesRepository;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieDetailsHandlerTest {

    @Mock
    MoviesRepository repository;

    @InjectMocks
    MovieDetailsHandler handler;

    @Test
    void whenNullCommandItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null));
    }

    @Test
    void whenInvalidIdItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new MovieDetailsQuery(null)));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new MovieDetailsQuery("")));
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new MovieDetailsQuery(" ")));
    }

    @Test
    void whenInvalidMovieCodeItReturnsEmpty() {
        //given
        var id = "invalid";
        var query = new MovieDetailsQuery(id);
        when(repository.getById(id)).thenReturn(Optional.empty());

        //when
        var result = handler.handle(query);

        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void whenValidMovieCodeItReturnsTheEntity() {
        //given
        var entity = MoviesObjectMother.getRandomMovie();
        var query = new MovieDetailsQuery(entity.getId());
        when(repository.getById(entity.getId())).thenReturn(Optional.of(entity));

        //when
        var result = handler.handle(query);

        //then
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

}
