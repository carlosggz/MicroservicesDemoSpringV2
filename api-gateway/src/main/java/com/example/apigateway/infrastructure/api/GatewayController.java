package com.example.apigateway.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import com.example.apigateway.application.ActorDetailsQuery;
import com.example.apigateway.application.LoginQuery;
import com.example.apigateway.domain.dtos.LoginRequestDto;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import com.example.apigateway.domain.models.ActorDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
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

    @PostMapping("/login")
    public Mono<LoginResponseDto> authenticate(@RequestBody LoginRequestDto loginRequestDto) {
        return pipeline
                .send(new LoginQuery(loginRequestDto))
                .onErrorMap(e -> new ResponseStatusException(e instanceof WebClientResponseException.Unauthorized
                        ? HttpStatus.UNAUTHORIZED
                        : HttpStatus.BAD_REQUEST));
    }
}
