package org.dci.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dci.domain.Genre;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MovieApiClient {
    private static final String API_KEY = System.getenv("MOVIE_API_KEY");
    private static final String API_URL_FETCH_MOVIE = "https://api.themoviedb.org/3";
    private static final String API_URL_FETCH_MOVIE_GENRES = "https://api.themoviedb.org/3/genre/movie/list?language=en";
    private static final String API_URL_FETCH_TV_GENRES = "https://api.themoviedb.org/3/genre/tv/list?language=en";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    public MovieDetails getMovieDetails(String movieName) {
        try {
            String searchUrl = API_URL_FETCH_MOVIE + "/search/movie?api_key="
                    + API_KEY + "&query=" + movieName.replace(" ", "%20")
                    + "&language=en-US&page=1";

            Request request = new Request.Builder()
                    .url(searchUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return parseMovieDetails(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MovieDetails parseMovieDetails(String jsonResponse) throws IOException {
        MovieApiResponse response = objectMapper.readerFor(MovieApiResponse.class).readValue(jsonResponse);
        return response.getResults().getFirst();
    }


    public Set<Genre> loadGenres() {
        Set<Genre> movieGenres = loadMoviesGenres();
        movieGenres.addAll(loadMoviesGenres());
        return movieGenres;
    }
    private Set<Genre> loadMoviesGenres() {
        Set<Genre> genres = new HashSet<>();
        try {
            String searchUrl = API_URL_FETCH_MOVIE_GENRES + "&api_key=" + API_KEY;

            Request request = new Request.Builder()
                    .url(searchUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                GenreApiResponse genreResponse = objectMapper
                        .readerFor(GenreApiResponse.class)
                        .readValue(response.body().string());

                genres.addAll(genreResponse.getGenres());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genres;

    }

    private Set<Genre> loadTVSeriesGenres() {
        Set<Genre> genres = new HashSet<>();
        try {
            String searchUrl = API_URL_FETCH_TV_GENRES + "&api_key=" + API_KEY;

            Request request = new Request.Builder()
                    .url(searchUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                GenreApiResponse genreResponse = objectMapper
                        .readerFor(GenreApiResponse.class)
                        .readValue(response.body().string());

                genres.addAll(genreResponse.getGenres());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genres;
    }
}