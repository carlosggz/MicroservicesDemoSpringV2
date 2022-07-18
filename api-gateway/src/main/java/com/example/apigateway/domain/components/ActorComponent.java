package com.example.apigateway.domain.components;

import com.example.apigateway.domain.Actor;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

public interface ActorComponent {
    Mono<Actor> getActor(@NotBlank String id);
}
