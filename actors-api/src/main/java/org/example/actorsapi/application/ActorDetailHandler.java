package org.example.actorsapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ActorDetailHandler implements Command.Handler<ActorDetailsQuery, Mono<Actor>> {

    private final ActorsRepository actorsRepository;

    @Override
    public Mono<Actor> handle(@NotNull ActorDetailsQuery command) {

        return Objects.isNull(command)
                ? Mono.error(new IllegalArgumentException())
                : actorsRepository.getById(command.getId());
    }
}
