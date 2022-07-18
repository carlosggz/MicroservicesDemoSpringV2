package com.example.apigateway.application;

import an.awesome.pipelinr.Command;
import com.example.apigateway.domain.ActorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
public class ActorDetailsQuery implements Command<Mono<ActorDetails>> {

    @NotEmpty
    @Getter
    final String id;
}
