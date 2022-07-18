package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchHandler implements Command.Handler<SearchQuery, List<MovieDto>> {

    final MoviesRepository repository;


    @Override
    public List<MovieDto> handle(@NotNull SearchQuery command) {
        Assert.notNull(command, "Invalid command");
        Assert.notNull(command.getSearchQuery(), "Invalid parameters");

        return repository.getSearch(command.getSearchQuery());
    }
}
