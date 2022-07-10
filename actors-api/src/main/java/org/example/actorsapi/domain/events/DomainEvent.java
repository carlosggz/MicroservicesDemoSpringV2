package org.example.actorsapi.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent {

    private String aggregateRootId;
    private String eventName;
    private LocalDateTime occurrenceDate;
}
