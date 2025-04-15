package org.dci.service;

import lombok.Setter;
import org.dci.client.HuggingFaceApiClient;
import org.dci.client.MovieApiClient;
import org.dci.domain.MovieDetails;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Setter
public class MovieRecommendationService {
    private HuggingFaceApiClient aiApi;
    private MovieApiClient movieApi;

    private static MovieRecommendationService instance = null;

    private MovieRecommendationService() {
        this.aiApi = new HuggingFaceApiClient();
        this.movieApi = new MovieApiClient();
    }

    public static MovieRecommendationService getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new MovieRecommendationService());
    }


    public List<MovieDetails> getMovieRecommendations(String userInput, int movieNumbers) {
        Set<String> movieNames = aiApi.getMovieRecommendation(userInput,  movieNumbers);
        if (movieNames.isEmpty()) {
            return Collections.emptyList();
        }
        return movieNames.stream().map(movieApi::getMovieDetails).toList();
    }
}
