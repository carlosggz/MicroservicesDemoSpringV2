package org.example.actorsapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.example.actorsapi.domain.actors.ActorsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ActorsHandler implements Command.Handler<ActorsQuery, Flux<ActorDto>> {

    private final ActorsRepository actorsRepository;

    @Override
    public Flux<ActorDto> handle(@NotNull ActorsQuery command) {
        return Objects.isNull(command)
                ? Flux.error(new IllegalArgumentException())
                : actorsRepository.getAll();
    }
}
