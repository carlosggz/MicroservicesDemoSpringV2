package org.example.moviesapi.infrastructure.api;

import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.moviesapi.application.LikeCommand;
import org.example.moviesapi.application.MovieDetailsQuery;
import org.example.moviesapi.application.MoviesQuery;
import org.example.moviesapi.domain.movies.Movie;
import org.example.moviesapi.domain.movies.MovieDto;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MoviesController.class)
class MoviesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private Pipeline pipeline;

    @Captor
    ArgumentCaptor<MovieDetailsQuery> detailsCaptor;

    @Captor
    ArgumentCaptor<LikeCommand> likeCaptor;

    @Test
    @SneakyThrows
    void getAllMoviesReturnsAListOfMovies() {
        //given
        var dtos = new Random()
                .ints(5)
                .mapToObj(x -> MoviesObjectMother.getRandomDto())
                .collect(Collectors.toList());
        when(pipeline.send(any(MoviesQuery.class))).thenReturn(dtos);

        //when/then

        var mvcResult = mockMvc
                .perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andReturn();

        var jsonResult = mvcResult.getResponse().getContentAsString();
        assertNotNull(jsonResult);

        var listResult = objectMapper.readValue(jsonResult, new TypeReference<List<MovieDto>>() {
        });
        assertNotNull(listResult);
        assertEquals(dtos.size(), listResult.size());

        listResult.forEach(r -> assertTrue(dtos.stream().anyMatch(d -> d.equals(r))));
    }

    @Test
    @SneakyThrows
    void getMovieReturns404ForInvalidMovieId() {
        //given
        var id = "invalid-id";
        when(pipeline.send(any(MovieDetailsQuery.class))).thenReturn(Optional.empty());

        //when/then
        mockMvc
                .perform(get("/api/v1/movies/" + id))
                .andExpect(status().isNotFound());

        verify(pipeline).send(detailsCaptor.capture());
        var query = detailsCaptor.getValue();
        assertNotNull(query);
        assertEquals(id, query.getId());
    }

    @Test
    @SneakyThrows
    void getMovieReturnsMovieDetailsForValidMovieId() {
        //given
        var givenMovie = MoviesObjectMother.getRandomMovie();
        when(pipeline.send(any(MovieDetailsQuery.class))).thenReturn(Optional.of(givenMovie));

        //when/then
        var mvcResult = mockMvc
                .perform(get("/api/v1/movies/" + givenMovie.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var jsonResult = mvcResult.getResponse().getContentAsString();
        assertNotNull(jsonResult);

        var movieResult = objectMapper.readValue(jsonResult, Movie.class);
        assertNotNull(movieResult);
        assertEquals(givenMovie, movieResult);
    }

    @Test
    @SneakyThrows
    void likeReturns404ForInvalidMovieId() {
        //given
        var id = "invalid-id";
        when(pipeline.send(any(LikeCommand.class))).thenReturn(false);

        //when/then
        mockMvc
                .perform(put("/api/v1/movies/like/" + id))
                .andExpect(status().isNotFound());

        verify(pipeline).send(likeCaptor.capture());
        var command = likeCaptor.getValue();
        assertNotNull(command);
        assertEquals(id, command.getId());
    }

    @Test
    @SneakyThrows
    void likeReturnsOkForValidMovieId() {
        //given
        var id = "valid-id";
        when(pipeline.send(any(LikeCommand.class))).thenReturn(true);

        //when/then
        mockMvc
                .perform(put("/api/v1/movies/like/" + id))
                .andExpect(status().isOk());

        verify(pipeline).send(likeCaptor.capture());
        var command = likeCaptor.getValue();
        assertNotNull(command);
        assertEquals(id, command.getId());
    }
}
