package com.example.apigateway.domain.components;

import com.example.apigateway.domain.dtos.LoginRequestDto;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public interface AuthComponent {
    Mono<LoginResponseDto> getToken(@NotNull LoginRequestDto loginRequestDto);
}
