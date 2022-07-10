package org.example.actorsapi.infrastructure.mappers;

import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.example.actorsapi.infrastructure.crud.ActorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ActorMapper {
    ActorMapper INSTANCE = Mappers.getMapper(ActorMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName")
    })
    ActorDto toDto(ActorEntity entity);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName")
    })
    ActorDto toDto(Actor entity);

    Actor toDomain(ActorEntity dbEntity);
    ActorEntity toEntity(Actor domainEntity);
}
