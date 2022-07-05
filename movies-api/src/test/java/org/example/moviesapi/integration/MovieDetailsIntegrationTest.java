package org.example.moviesapi.integration;

import org.example.moviesapi.application.MovieDetailsHandler;
import org.example.moviesapi.application.MovieDetailsQuery;
import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MovieDetailsIntegrationTest {

    @Autowired
    MoviesCrudRepository crudRepository;

    @Autowired
    MovieDetailsHandler handler;

    @BeforeEach
    void setup() {
        crudRepository.deleteAll();
    }

    @Test
    void getDetailsWithInvalidCodeReturnsEmptyResult() {
        //when
        var result = handler.handle(new MovieDetailsQuery("invalid-id"));

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void getDetailsWithValidCodeReturnsMovie() {
        //given
        var dbEntity = MoviesObjectMother.getRandomDbEntity();
        var domainEntity = MovieMapper.INSTANCE.toDomain(dbEntity);
        crudRepository.save(dbEntity);

        //when
        var result = handler.handle(new MovieDetailsQuery(domainEntity.getId()));

        //then
        assertTrue(result.isPresent());
        assertEquals(domainEntity, result.get());
    }

}
