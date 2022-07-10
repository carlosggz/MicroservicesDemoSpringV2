package org.example.actorsapi.infrastructure.migrations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.actorsapi.domain.actors.Actor;
import org.example.actorsapi.infrastructure.config.Constants;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@ChangeUnit(id="InitialDataChangelog", order = "1", author = "cgg")
@RequiredArgsConstructor
@Slf4j
public class InitialData {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Execution
    public void changeSet() throws IOException {
        mongoTemplate.createCollection(Constants.ACTORS_COLLECTION_NAME);

        var path = ResourceUtils.getFile("classpath:migrations/V_01_actors.json").toPath();
        var json = Files.readString(path);
        var actors = objectMapper.readValue(json, new TypeReference<List<Actor>>() {});

        actors.forEach(a -> mongoTemplate.save(a, Constants.ACTORS_COLLECTION_NAME));
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.dropCollection(Constants.ACTORS_COLLECTION_NAME);
    }
}
