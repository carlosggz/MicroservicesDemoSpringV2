package org.example.moviesapi.domain.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.moviesapi.domain.movies.Movie;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class LikeMovieEvent extends DomainEvent{
    public static final String EVENT_NAME = "movie.like";
    private int likes;

    public LikeMovieEvent(@NotNull Movie movie) {
        super(movie.getId(), EVENT_NAME);
        this.likes = movie.getLikes();
    }
}
