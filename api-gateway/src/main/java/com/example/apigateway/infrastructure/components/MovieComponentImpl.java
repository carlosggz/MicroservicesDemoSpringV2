package com.example.apigateway.infrastructure.components;

import com.example.apigateway.domain.components.MovieComponent;
import com.example.apigateway.domain.dtos.SearchCriteriaDto;
import com.example.apigateway.domain.models.Movie;
import com.example.apigateway.domain.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MovieComponentImpl implements MovieComponent {

    private final WebClient moviesWebClient;
    private final AppSettings appSettings;

    @Override
    public Flux<Movie> getMovies(@NotNull Collection<String> movieCodes) {

        if (Objects.isNull(movieCodes)) {
                return Flux.error(new IllegalArgumentException("Invalid movies codes"));
        }

        var codes = movieCodes.stream().filter(StringUtils::isNotBlank).toList();

        return codes.size() == 0
                ? Flux.empty()
                : moviesWebClient
                    .post()
                    .uri(appSettings.getMoviesSearchPath())
                    .body((BodyInserters.fromValue(SearchCriteriaDto.builder().ids(movieCodes).build())))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Movie.class);
    }
}
