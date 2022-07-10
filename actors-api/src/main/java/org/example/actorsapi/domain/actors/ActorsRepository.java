package org.example.actorsapi.domain.actors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface ActorsRepository {
    Flux<ActorDto> getAll();
    Mono<Actor> getById(@NotEmpty String id);
    Mono<Actor> save(@NotNull Actor actor);
    Flux<Actor> getActorsInMovie(@NotEmpty String movieId);
}
