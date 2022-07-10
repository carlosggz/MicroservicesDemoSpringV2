package org.example.actorsapi.utils;

import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.domain.actors.ActorDto;
import org.example.actorsapi.infrastructure.crud.ActorEntity;
import org.example.actorsapi.infrastructure.mappers.ActorMapper;

import java.util.Set;
import java.util.UUID;

@UtilityClass
public class ActorsObjectMother {
    static final Faker faker = new Faker();

    public Actor getRandomActor() {
        return Actor.builder()
                .id(UUID.randomUUID().toString())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .character(faker.name().title())
                .likes(0)
                .movies(Set.of(faker.oscarMovie().movieName()))
                .build();
    }

    public ActorDto getRandomDto() {
        return new ActorDto(UUID.randomUUID().toString(), faker.name().firstName(), faker.name().lastName());
    }

    public ActorEntity getRandomDbEntity() { return ActorMapper.INSTANCE.toEntity(getRandomActor());}
}
