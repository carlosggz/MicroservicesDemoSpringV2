package org.example.moviesapi.domain.movies;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface MoviesRepository {
    List<MovieDto> getAll();
    List<MovieDto> getSearch(@NotNull SearchCriteriaDto searchCriteria);
    Optional<Movie> getById(@NotEmpty String id);
    void save(@NotNull Movie movie);
}

