package org.example.actorsapi.domain.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LikeMovieEvent extends DomainEvent {
    private int likes;

    @Builder
    public LikeMovieEvent(
            String aggregateRootId,
            String eventName,
            LocalDateTime occurrenceDate,
            int likes
            ) {
        super(aggregateRootId, eventName, occurrenceDate);
        this.likes = likes;
    }

}
