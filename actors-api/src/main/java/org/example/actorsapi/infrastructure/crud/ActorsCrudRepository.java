package org.example.actorsapi.infrastructure.crud;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ActorsCrudRepository extends ReactiveMongoRepository<ActorEntity, String> {
}
