package com.example.apigateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class ActorDetails {
    String id;
    String firstName;
    String lastName;
    String character;
    int likes;
    Set<Movie> movies;
}
