package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.moviesapi.domain.movies.Movie;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@RequiredArgsConstructor
public class MovieDetailsQuery implements Command<Optional<Movie>> {

    @NotEmpty
    @Getter
    private final String id;
}
