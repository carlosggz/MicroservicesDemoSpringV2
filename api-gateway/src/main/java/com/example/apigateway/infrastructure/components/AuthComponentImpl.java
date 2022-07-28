package com.example.apigateway.infrastructure.components;

import com.example.apigateway.domain.components.AuthComponent;
import com.example.apigateway.domain.dtos.LoginRequestDto;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import com.example.apigateway.domain.settings.AuthSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthComponentImpl implements AuthComponent {
    private final WebClient authWebClient;
    private final AuthSettings authSettings;

    @Override
    public Mono<LoginResponseDto> getToken(@NotNull LoginRequestDto loginRequestDto) {
        return Objects.isNull(loginRequestDto)
                ? Mono.error(new IllegalArgumentException("Invalid auth settings"))
                : authWebClient
                    .post()
                    .uri(authSettings.getAuthUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(getBody(loginRequestDto)))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(LoginResponseDto.class);
    }

    private MultiValueMap<String, String> getBody(LoginRequestDto loginRequestDto) {
        return new LinkedMultiValueMap<>(Map.of(
                "grant_type", List.of(authSettings.getGrantType()),
                "client_id", List.of(authSettings.getClientId()),
                "client_secret", List.of(authSettings.getClientSecret()),
                "username", List.of(loginRequestDto.getUsername()),
                "password", List.of(loginRequestDto.getPassword()),
                "scope", List.of(authSettings.getScope())
        ));
    }
}
