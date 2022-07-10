package org.example.actorsapi.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;
import org.example.actorsapi.application.ActorDetailsQuery;
import org.example.actorsapi.application.ActorsQuery;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/actors")
public class ActorsController {

    private final Pipeline pipeline;

    @GetMapping("")
    public Flux<ActorDto> getAllActors() {
        return pipeline.send(new ActorsQuery());
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Actor>> getActor(@PathVariable String id) {
        return pipeline
                .send(new ActorDetailsQuery(id))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
