package com.example.apigateway.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import com.example.apigateway.application.ActorDetailsQuery;
import com.example.apigateway.domain.ActorDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
public class GatewayController {

    private final Pipeline pipeline;

    @GetMapping("/actor/{id}")
    public Mono<ResponseEntity<ActorDetails>> getActorDetails(@PathVariable("id") @NotBlank String id) {
        return pipeline
                .send(new ActorDetailsQuery(id))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
