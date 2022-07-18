package com.example.apigateway.application;

import an.awesome.pipelinr.Command;
import com.example.apigateway.domain.Actor;
import com.example.apigateway.domain.ActorDetails;
import com.example.apigateway.domain.AppSettings;
import com.example.apigateway.domain.Movie;
import com.example.apigateway.domain.components.ActorComponent;
import com.example.apigateway.domain.components.MovieComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorDetailHandler implements Command.Handler<ActorDetailsQuery, Mono<ActorDetails>> {

    private final AppSettings appSettings;
    private final ActorComponent actorComponent;
    private final MovieComponent movieComponent;

    @Override
    public Mono<ActorDetails> handle(@NotNull ActorDetailsQuery command) {

        if (Objects.isNull(command) || StringUtils.isBlank(command.getId())) {
                return Mono.error(new IllegalArgumentException());
        }

        return actorComponent.getActor(command.getId())
                .zipWhen(x -> movieComponent
                        .getMovies(x.getMovies())
                        .collectList())
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

    private ActorDetails getDetails(Actor actor, Collection<Movie> movies) {
        return ActorDetails.builder()
                .id(actor.getId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .character(actor.getCharacter())
                .likes(actor.getLikes())
                .movies(Set.copyOf(movies))
                .build();
    }



}
