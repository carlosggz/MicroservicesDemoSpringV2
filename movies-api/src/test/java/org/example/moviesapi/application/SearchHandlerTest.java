package org.example.moviesapi.application;

import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.example.moviesapi.domain.movies.SearchCriteriaDto;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHandlerTest {

    @Mock
    MoviesRepository repository;

    @InjectMocks
    SearchHandler handler;

    @Test
    void whenNullCommandItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null));
    }

    @Test
    void whenInvalidCommandItThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new SearchQuery(null)));
    }

    @Test
    void whenNoResultsReturnsEmpty() {
        //given
        var criteria = SearchCriteriaDto.builder().id("123").build();
        when(repository.getSearch(criteria)).thenReturn(List.of());

        //when
        var result = handler.handle(new SearchQuery(criteria));

        //then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void whenResultsReturnsThem() {
        //given
        var dtos = List.of(
                MoviesObjectMother.getRandomDto(),
                MoviesObjectMother.getRandomDto(),
                MoviesObjectMother.getRandomDto()
        );
        var criteria = SearchCriteriaDto.builder()
                .ids(dtos.stream().map(MovieDto::id).toList())
                .build();
        when(repository.getSearch(criteria)).thenReturn(dtos);

        //when
        var result = handler.handle(new SearchQuery(criteria));

        //then
        assertNotNull(result);
        assertEquals(dtos.size(), result.size());
        result.forEach(r -> assertTrue(dtos.stream().anyMatch(d -> d.equals(r))));
    }
}
