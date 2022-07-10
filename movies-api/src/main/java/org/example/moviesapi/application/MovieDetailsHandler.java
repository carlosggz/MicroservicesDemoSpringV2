package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MovieDetailsHandler implements Command.Handler<MovieDetailsQuery, Optional<Movie>> {

    final MoviesRepository repository;

    @Override
    public Optional<Movie> handle(@NotNull MovieDetailsQuery query) {
        Assert.notNull(query, "Invalid query");
        Assert.isTrue(StringUtils.isNotBlank(query.getId()), "Invalid Id");

        return repository.getById(query.getId());
    }
}
