package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.domain.movies.SearchCriteriaDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RequiredArgsConstructor
public class SearchQuery implements Command<List<MovieDto>> {
    @NotEmpty
    @Getter
    private final SearchCriteriaDto searchQuery;
}
