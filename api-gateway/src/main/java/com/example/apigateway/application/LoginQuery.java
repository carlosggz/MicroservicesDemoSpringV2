package com.example.apigateway.application;

import an.awesome.pipelinr.Command;
import com.example.apigateway.domain.dtos.LoginRequestDto;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
public class LoginQuery implements Command<Mono<LoginResponseDto>> {

    @NotEmpty
    @Getter
    final LoginRequestDto loginRequestDto;
}
