package org.example.moviesapi.application;

import org.example.moviesapi.domain.movies.MoviesRepository;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoviesHandlerTest {

    @Mock
    MoviesRepository repository;

    @InjectMocks
    MoviesHandler handler;

    @Test
    void whenNullCommandItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null));
    }

    @Test
    void getMoviesReturnsAList() {
        //given
        var entities = new Random()
                .ints(5)
                .mapToObj(x -> MoviesObjectMother.getRandomDto())
                .collect(Collectors.toList());
        when(repository.getAll()).thenReturn(entities);

        //when
        var result = handler.handle(new MoviesQuery());

        //then
        assertNotNull(result);
        assertEquals(entities.size(), result.size());

        for(var index = 0; index < entities.size(); index++) {
            assertEquals(entities.get(index), result.get(index));
        }
    }

}
