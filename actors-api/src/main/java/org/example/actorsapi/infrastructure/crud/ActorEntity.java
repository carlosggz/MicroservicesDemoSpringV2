package org.example.actorsapi.infrastructure.crud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.actorsapi.infrastructure.config.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Document(collection = Constants.ACTORS_COLLECTION_NAME)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorEntity {
    @Id
    private String id;

    @Field
    private String firstName;

    @Field
    private String lastName;

    @Field
    private String character;

    @Field
    @Builder.Default
    private int likes = 0;

    @Field
    @Builder.Default
    Set<String> movies = new HashSet<>();

}
