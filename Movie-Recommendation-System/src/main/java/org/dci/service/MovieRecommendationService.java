package org.dci.service;

import lombok.Setter;
import org.dci.client.HuggingFaceApiClient;
import org.dci.client.MovieApiClient;
import org.dci.domain.Actor;
import org.dci.domain.Genre;
import org.dci.client.MovieDetails;
import org.dci.repository.GenreRepository;

import java.util.*;

@Setter
public class MovieRecommendationService {
    private HuggingFaceApiClient aiApi;
    private MovieApiClient movieApi;
    private static final GenreRepository genreRepository = GenreRepository.getInstance();

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
        Set<String> movieNames = aiApi.getMovieRecommendation(userInput, movieNumbers);
        if (movieNames.isEmpty()) {
            return Collections.emptyList();
        }
        return movieNames.stream().map(movieApi::getMovieDetails).toList();
    }

    public void populateGenres() {
        if (genreRepository.isGenresEmpty()) {
            Set<Genre> genres = movieApi.loadGenres();
           genres.forEach(genre -> {genreRepository.addNewGenre(genre);});
        }
    }

    public List<Actor> getMovieActor(int movieId) {
        return movieApi.getMovieActors(movieId);
    }

}
