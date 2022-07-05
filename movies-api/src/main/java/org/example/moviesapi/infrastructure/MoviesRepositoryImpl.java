package org.example.moviesapi.infrastructure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.example.moviesapi.infrastructure.jpa.MoviesCrudRepository;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class MoviesRepositoryImpl implements MoviesRepository {

    private final MoviesCrudRepository repository;

    @Override
    public List<MovieDto> getAll() {
        return repository
                .findAll()
                .stream()
                .map(MovieMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Movie> getById(@NotEmpty String id) {

        return repository
                .findById(id)
                .map(MovieMapper.INSTANCE::toDomain);
    }

    @Override
    public void save(@NotNull Movie movie) {
        val entity = MovieMapper.INSTANCE.toEntity(movie);
        repository.save(entity);
    }
}
