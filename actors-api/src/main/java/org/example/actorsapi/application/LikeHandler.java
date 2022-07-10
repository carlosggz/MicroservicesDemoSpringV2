package org.example.actorsapi.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.example.actorsapi.domain.actors.ActorsRepository;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeHandler {

    private final ActorsRepository repository;

    //@Transactional
    public void handle(@NotNull LikeMovieEvent event) {
        Assert.notNull(event, "Invalid event");
        Assert.isTrue(StringUtils.isNotBlank(event.getAggregateRootId()), "Invalid event id");

        log.info("Received like event for movie: {}", event.getAggregateRootId());

        var result = repository
                .getActorsInMovie(event.getAggregateRootId())
                .doOnNext(x -> x.setLikes(x.getLikes() + 1))
                .flatMap(repository::save)
                .collectList()
                .map(List::size)
                .block();

        log.info("Updated {} actor(s)", result);

    }
}
