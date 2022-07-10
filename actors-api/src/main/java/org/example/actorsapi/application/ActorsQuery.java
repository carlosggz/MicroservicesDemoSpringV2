package org.example.actorsapi.application;

import an.awesome.pipelinr.Command;
import org.example.actorsapi.domain.actors.ActorDto;
import reactor.core.publisher.Flux;

public class ActorsQuery implements Command<Flux<ActorDto>> {
}
