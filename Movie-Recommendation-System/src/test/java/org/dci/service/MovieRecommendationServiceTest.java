package org.dci.service;

import org.dci.client.HuggingFaceApiClient;
import org.dci.client.MovieApiClient;
import org.dci.client.MovieDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRecommendationServiceTest {

    @Mock
    private HuggingFaceApiClient aiApi;

    @Mock
    private MovieApiClient movieApi;

    @InjectMocks
    private MovieRecommendationService movieRecommendationService = MovieRecommendationService.getInstance();

    @BeforeEach
    void setup() {

    }

    @Test
    void testGetMovieRecommendations() {
        String userInput = "science fiction";
        int movieNumbers = 2;
        Set<String> movieNames = Set.of("Interstellar", "Inception");

        when(aiApi.getMovieRecommendation(userInput, movieNumbers)).thenReturn(movieNames);
        MovieDetails movieDetails1 = new MovieDetails("Interstellar", "Sci-Fi", "2014", "8.7");
        when(movieApi.getMovieDetails("Interstellar")).thenReturn(movieDetails1);
        MovieDetails movieDetails2 = new MovieDetails("Inception", "Sci-Fi", "2010", "8.5");
        when(movieApi.getMovieDetails("Inception")).thenReturn(movieDetails2);


        List<MovieDetails> recommendations = movieRecommendationService.getMovieRecommendations(userInput, movieNumbers);

        assertEquals(2, recommendations.size());
        assertEquals(recommendations, Arrays.asList(movieDetails1, movieDetails2));

        verify(aiApi, times(1)).getMovieRecommendation(userInput, movieNumbers);
        verify(movieApi, times(1)).getMovieDetails("Interstellar");
        verify(movieApi, times(1)).getMovieDetails("Inception");

    }

}