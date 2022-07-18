package com.example.apigateway.application;

import an.awesome.pipelinr.Command;
import com.example.apigateway.domain.Actor;
import com.example.apigateway.domain.ActorDetails;
import com.example.apigateway.domain.AppSettings;
import com.example.apigateway.domain.Movie;
import com.example.apigateway.domain.SearchCriteriaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorDetailHandler implements Command.Handler<ActorDetailsQuery, Mono<ActorDetails>> {

    private final AppSettings appSettings;
    private final WebClient moviesWebClient;
    private final WebClient actorsWebClient;

    @Override
    public Mono<ActorDetails> handle(@NotNull ActorDetailsQuery command) {

        if (Objects.isNull(command) || StringUtils.isBlank(command.getId())) {
                return Mono.error(new IllegalArgumentException());
        }

        return getActor(command.getId())
                .zipWhen(x -> getMovies(x.getMovies()))
                .map(x -> getDetails(x.getT1(), x.getT2()))
                .timeout(Duration.ofSeconds(appSettings.getTimeoutSeconds()))
                .retryWhen(Retry
                        .backoff(appSettings.getBackoffRetries(), Duration.ofSeconds(appSettings.getBackoffSeconds()))
                        .onRetryExhaustedThrow((spec, signal) -> {
                            log.error("Error obtaining data top build response: {}", signal.failure().getMessage());
                            return new TimeoutException("Timeout generating response ");
                        })
                );
    }

    private ActorDetails getDetails(Actor actor, Set<Movie> movies) {
        return ActorDetails.builder()
                .id(actor.getId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .character(actor.getCharacter())
                .likes(actor.getLikes())
                .movies(movies)
                .build();
    }
    private Mono<Actor> getActor(String id) {
        return actorsWebClient
                .get()
                .uri(appSettings.getActorDetailsPath().replace("{ACTOR-ID}", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Actor.class);
    }

    private Mono<Set<Movie>> getMovies(Set<String> movieCodes) {
        return moviesWebClient
                .post()
                .uri(appSettings.getMoviesSearchPath())
                .body((BodyInserters.fromValue(SearchCriteriaDto.builder().ids(movieCodes).build())))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Movie.class)
                .collect(Collectors.toSet());
    }
}
