package org.example.actorsapi.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.example.actorsapi.domain.actors.ActorsRepository;
import org.example.actorsapi.infrastructure.crud.ActorsCrudRepository;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ActorsRepositoryImpl implements ActorsRepository {

    private final ActorsCrudRepository repository;

    @Override
    public Flux<ActorDto> getAll() {
        return repository
                .findAll()
                .map(ActorMapper.INSTANCE::toDto);
    }

    @Override
    public Mono<Actor> getById(@NotEmpty String id) {
        return repository
                .findById(id)
                .map(ActorMapper.INSTANCE::toDomain);
    }

    @Override
    public Mono<Actor> save(@NotNull Actor actor) {
        if (Objects.isNull(actor)) return Mono.empty();

        return repository
                .save(ActorMapper.INSTANCE.toEntity(actor))
                .map(ActorMapper.INSTANCE::toDomain);
    }

    @Override
    public Flux<Actor> getActorsInMovie(@NotEmpty String movieId) {
        return repository
                .findAll()
                .filter(x -> Objects.nonNull(x.getMovies()) && x.getMovies().contains(movieId))
                .map(ActorMapper.INSTANCE::toDomain);
    }
}
