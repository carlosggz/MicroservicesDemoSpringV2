package org.example.moviesapi.utils;

import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.infrastructure.jpa.MovieEntity;
import org.example.moviesapi.infrastructure.mappers.MovieMapper;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class MoviesObjectMother {

    static final Faker faker = new Faker();
    static final int currentYear = LocalDate.now().getYear();

    public Movie getRandomMovie() {
        return Movie.builder()
                .id(UUID.randomUUID().toString())
                .title(StringUtils.substring(faker.name().title(), 1, 50))
                .imdb(StringUtils.substring(faker.beer().name(), 1, 20))
                .likes(0)
                .releaseYear(faker.random().nextInt(currentYear-100, currentYear))
                .build();
    }

    public MovieDto getRandomDto() {
        return new MovieDto(UUID.randomUUID().toString(), faker.name().title());
    }

    public MovieEntity getRandomDbEntity() { return MovieMapper.INSTANCE.toEntity(getRandomMovie());}
}
