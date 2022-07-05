package org.example.moviesapi.infrastructure.mappers;

import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.infrastructure.jpa.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title")
    })
    MovieDto toDto(MovieEntity entity);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title")
    })
    MovieDto toDto(Movie entity);

    Movie toDomain(MovieEntity dbEntity);
    MovieEntity toEntity(Movie domainEntity);
}
