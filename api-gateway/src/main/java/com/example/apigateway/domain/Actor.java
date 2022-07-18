package com.example.apigateway.domain;

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

    String id;
    String firstName;
    String lastName;
    String character;

    @Builder.Default
    int likes = 0;

    @Builder.Default
    Set<String> movies = new HashSet<>();
}
