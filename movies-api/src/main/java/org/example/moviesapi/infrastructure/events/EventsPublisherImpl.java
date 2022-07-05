package org.example.moviesapi.infrastructure.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.moviesapi.domain.events.DomainEvent;
import org.example.moviesapi.domain.events.EventsPublisher;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventsPublisherImpl implements EventsPublisher {

    public static final String BINDING = "like-out-0";
    public static final String ROUTING_KEY = "myRoutingKey";
    private final StreamBridge streamBridge;

    @Override
    public <T extends DomainEvent> void publish(T domainEvent) {
        log.info("Publishing event of type {} and id {}", domainEvent.getEventName(), domainEvent.getAggregateRootId());
        var message = MessageBuilder
                .withPayload(domainEvent)
                .setHeader(ROUTING_KEY, domainEvent.getEventName())
                .build();
        streamBridge.send(BINDING, message);
    }
}
