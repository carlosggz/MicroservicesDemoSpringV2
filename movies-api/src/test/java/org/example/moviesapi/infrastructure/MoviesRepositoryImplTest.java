package org.example.moviesapi.infrastructure;

import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class MoviesRepositoryImplTest {

    @Autowired
    MoviesCrudRepository crudRepository;

    MoviesRepositoryImpl moviesRepository;

    @BeforeEach
    void setup() {
        moviesRepository = new MoviesRepositoryImpl(crudRepository);
    }

    @AfterEach
    void cleanUp() {
        crudRepository.deleteAll();
        moviesRepository = null;
    }

    @Test
    void getAllReturnsAllEntitiesOnDatabase() {
        //given
        var dtos = Stream.of(
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity()
                )
                .peek(crudRepository::save)
                .map(MovieMapper.INSTANCE::toDto)
                .toList();

        //when
        var result = moviesRepository.getAll();

        //then
        assertNotNull(result);
        assertEquals(dtos.size(), result.size());

        result.forEach(r -> assertTrue(dtos.stream().anyMatch(d -> d.equals(r))));
    }


}
