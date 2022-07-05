package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.moviesapi.domain.events.EventsPublisher;
import org.example.moviesapi.domain.events.LikeMovieEvent;
import org.example.moviesapi.domain.movies.MoviesRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Component
public class LikeHandler implements Command.Handler<LikeCommand, Boolean> {

    final MoviesRepository repository;
    final EventsPublisher eventsPublisher;

    @Override
    public Boolean handle(@NotNull LikeCommand command)  {
        Assert.notNull(command, "Invalid command");
        Assert.isTrue(StringUtils.isNotBlank(command.getId()), "Invalid Id");

        var value = repository.getById(command.getId());

        if (value.isEmpty())
            return false;

        var entity = value.get();
        entity.setLikes(entity.getLikes()+1);
        repository.save(entity);

        eventsPublisher.publish(new LikeMovieEvent(entity));

        return true;
    }
}
