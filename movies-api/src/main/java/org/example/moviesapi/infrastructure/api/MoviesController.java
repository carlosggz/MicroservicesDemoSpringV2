package org.example.moviesapi.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.moviesapi.application.LikeCommand;
import org.example.moviesapi.application.MovieDetailsQuery;
import org.example.moviesapi.application.MoviesQuery;
import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MovieDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/movies")
public class MoviesController {

    private final Pipeline pipeline;

    @GetMapping("")
    public List<MovieDto> getAllMovies() {
        return pipeline.send(new MoviesQuery());
    }

    @GetMapping("{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable String id) {
        return pipeline
                .send(new MovieDetailsQuery(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("like/{id}")
    public ResponseEntity<?> like(@PathVariable String id) {
        val result = pipeline.send(new LikeCommand(id));
        return result ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}
