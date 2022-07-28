package com.example.apigateway.application;

import an.awesome.pipelinr.Command;
import com.example.apigateway.domain.components.AuthComponent;
import com.example.apigateway.domain.dtos.LoginResponseDto;
import com.example.apigateway.domain.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginHandler implements Command.Handler<LoginQuery, Mono<LoginResponseDto>> {

    private final AppSettings appSettings;
    private final AuthComponent authComponent;

    @Override
    public Mono<LoginResponseDto> handle(@NotNull LoginQuery command) {

        if (Objects.isNull(command) || Objects.isNull(command.getLoginRequestDto()) ||
                StringUtils.isBlank(command.getLoginRequestDto().getUsername()) ||
                StringUtils.isBlank(command.getLoginRequestDto().getPassword())) {
                return Mono.error(new IllegalArgumentException());
        }

        return authComponent.getToken(command.getLoginRequestDto())
                .timeout(Duration.ofSeconds(appSettings.getTimeoutSeconds()));
    }
}
