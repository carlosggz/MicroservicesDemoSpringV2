package org.example.moviesapi.infrastructure.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {
    @Id
    @Column(length = 50, updatable = false, nullable = false )
    private String id;

    @Column(length = 500, nullable = false )
    private String title;

    private int releaseYear;

    @Column(length = 20, nullable = false )
    private String imdb;

    @Builder.Default
    private int likes = 0;
}

