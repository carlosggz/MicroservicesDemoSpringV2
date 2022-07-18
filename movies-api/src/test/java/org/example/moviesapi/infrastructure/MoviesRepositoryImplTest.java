package org.example.moviesapi.infrastructure;

import org.example.moviesapi.domain.movies.SearchCriteriaDto;
import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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

    @Test
    void searchWithNullCommandReturnsEmptyList() {
        //when
        var result = moviesRepository.getSearch(null);

        //then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void searchWithNonExistentIdsReturnsEmptyList() {
        //when
        var result = moviesRepository.getSearch(SearchCriteriaDto.builder()
                .id("1234")
                .build());

        //then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void searchWithValidIdsReturnsTheMovies() {
        //given
        var dtos = Stream.of(
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity(),
                        MoviesObjectMother.getRandomDbEntity()
                )
                .peek(crudRepository::save)
                .map(MovieMapper.INSTANCE::toDto)
                .toList();
        var query = SearchCriteriaDto.builder()
                .id(dtos.get(0).id())
                .id(dtos.get(2).id())
                .build();

        //when
        var result = moviesRepository.getSearch(query);

        //then
        assertNotNull(result);
        assertEquals(2, result.size());

        Stream
                .of(dtos.get(0), dtos.get(2))
                .forEach(d -> assertTrue(result.stream().anyMatch(d::equals)));
    }

    @Test
    void getByIdWithInvalidIdReturnsEmpty() {
        assertTrue(moviesRepository.getById(null).isEmpty());
        assertTrue(moviesRepository.getById("").isEmpty());
        assertTrue(moviesRepository.getById("  ").isEmpty());
        assertTrue(moviesRepository.getById("invalid-id").isEmpty());
    }

    @Test
    void getByIdWithExistingIdReturnsMovie() {
        //given
        var movie = MoviesObjectMother.getRandomMovie();
        crudRepository.save(MovieMapper.INSTANCE.toEntity(movie));

        //when
        var result = moviesRepository.getById(movie.getId());

        //then
        assertTrue(result.isPresent());
        assertEquals(movie, result.get());
    }

    @Test
    void saveWithNullEntityDoesNothing() {
        //given
        var crudMock = Mockito.mock(MoviesCrudRepository.class);
        var repo = new MoviesRepositoryImpl(crudMock);

        //when
        repo.save(null);

        //then
        verifyNoInteractions(crudMock);
    }

    @Test
    void saveWithEntityAddsItToDatabase() {
        //given
        var movie = MoviesObjectMother.getRandomMovie();

        //when
        moviesRepository.save(movie);

        //then
        var result = crudRepository.findAll();
        assertEquals(1, result.size());
        assertEquals(MovieMapper.INSTANCE.toEntity(movie), result.get(0));
    }


}
