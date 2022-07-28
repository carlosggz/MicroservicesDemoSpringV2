package com.example.apigateway.infrastructure.components;

import com.example.apigateway.domain.components.ActorComponent;
import com.example.apigateway.domain.models.Actor;
import com.example.apigateway.domain.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

@Component
@RequiredArgsConstructor
public class ActorComponentImpl implements ActorComponent {

    private final WebClient actorsWebClient;
    private final AppSettings appSettings;

    @Override
    public Mono<Actor> getActor(@NotBlank String id) {
        return StringUtils.isBlank(id)
                ? Mono.error(new IllegalArgumentException("Invalid actor id"))
                : actorsWebClient
                    .get()
                    .uri(appSettings.getActorDetailsPath().replace("{ACTOR-ID}", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Actor.class);
    }
}
