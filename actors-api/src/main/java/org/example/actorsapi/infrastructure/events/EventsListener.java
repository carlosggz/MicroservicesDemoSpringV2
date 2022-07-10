package org.example.actorsapi.infrastructure.events;

import lombok.RequiredArgsConstructor;
import org.example.actorsapi.application.LikeHandler;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class EventsListener {

    private final LikeHandler handler;

    @Bean
    public Consumer<LikeMovieEvent> likeListener() {
        return handler::handle;
    }
}
