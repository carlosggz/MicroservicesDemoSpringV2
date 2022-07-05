package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MoviesHandler implements Command.Handler<MoviesQuery, List<MovieDto>> {

    final MoviesRepository repository;


    @Override
    public List<MovieDto> handle(@NotNull MoviesQuery command) {
        Assert.notNull(command, "Invalid command");
        return repository.getAll();
    }
}
