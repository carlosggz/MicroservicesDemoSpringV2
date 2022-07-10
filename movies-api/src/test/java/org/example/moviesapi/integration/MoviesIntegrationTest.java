package org.example.moviesapi.integration;

import org.example.moviesapi.application.MoviesHandler;
import org.example.moviesapi.application.MoviesQuery;
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
public class MoviesIntegrationTest {

    @Autowired
    MoviesCrudRepository crudRepository;

    @Autowired
    MoviesHandler handler;

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

        //when
        var result = handler.handle(new MoviesQuery());

        //then
        assertNotNull(result);
        assertEquals(expectedResult.size(), result.size());

        result.forEach(r -> assertTrue(expectedResult.stream().anyMatch(e -> e.equals(r))));
    }

}
