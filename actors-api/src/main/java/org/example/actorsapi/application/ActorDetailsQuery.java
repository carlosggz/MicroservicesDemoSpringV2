package org.example.actorsapi.application;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.actorsapi.domain.actors.Actor;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
public class ActorDetailsQuery implements Command<Mono<Actor>> {

    @NotEmpty
    @Getter
    final String id;
}
