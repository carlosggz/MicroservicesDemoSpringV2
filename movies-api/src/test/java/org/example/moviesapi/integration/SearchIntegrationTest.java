package org.example.moviesapi.integration;

import org.example.moviesapi.application.MoviesHandler;
import org.example.moviesapi.application.MoviesQuery;
import org.example.moviesapi.application.SearchHandler;
import org.example.moviesapi.application.SearchQuery;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.SearchCriteriaDto;
import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SearchIntegrationTest {

    @Autowired
    MoviesCrudRepository crudRepository;

    @Autowired
    SearchHandler handler;

    @BeforeEach
    void setup() {
        crudRepository.deleteAll();
    }

    @Test
    void getMoviesReturnsAListOfDtos() {
        //given
        var expectedResult = Stream.of(
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity()
                )
                .peek(crudRepository::save)
                .map(MovieMapper.INSTANCE::toDto)
                .toList();
        var criteria = SearchCriteriaDto.builder()
                .ids(expectedResult.stream().map(MovieDto::id).toList())
                .build();

        //when
        var result = handler.handle(new SearchQuery(criteria));

        //then
        assertNotNull(result);
        assertEquals(expectedResult.size(), result.size());

        result.forEach(r -> assertTrue(expectedResult.stream().anyMatch(e -> e.equals(r))));
    }

}
