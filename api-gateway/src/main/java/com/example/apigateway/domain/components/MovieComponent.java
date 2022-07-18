package com.example.apigateway.domain.components;

import com.example.apigateway.domain.Movie;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.util.Collection;

public interface MovieComponent {
    Flux<Movie> getMovies(@NotNull Collection<String> movieCodes);
}
