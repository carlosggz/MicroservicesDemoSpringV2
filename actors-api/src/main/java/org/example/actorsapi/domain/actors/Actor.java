package org.example.actorsapi.domain.actors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class Actor {

    @NotNull
    @Size(min = 1, max = 50)
    String id;

    @NotNull
    @Size(min = 1, max = 50)
    String firstName;
    @NotNull
    @Size(min = 1, max = 50)
    String lastName;

    @NotNull
    @Size(min = 1, max = 100)
    String character;

    @Builder.Default
    int likes = 0;

    @Builder.Default
    Set<String> movies = new HashSet<>();
}
