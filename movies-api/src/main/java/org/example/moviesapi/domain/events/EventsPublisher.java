package org.example.moviesapi.domain.events;

import javax.validation.constraints.NotNull;

public interface EventsPublisher {
    <T extends DomainEvent> void publish(@NotNull T domainEvent);
}
