package org.example.moviesapi.domain.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public abstract class DomainEvent {

    private String aggregateRootId;
    private String eventName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime occurrenceDate;

    protected DomainEvent(String aggregateRootId, String eventName) {
        this.aggregateRootId = aggregateRootId;
        this.occurrenceDate = LocalDateTime.now();
        this.eventName = eventName;
    }
}
