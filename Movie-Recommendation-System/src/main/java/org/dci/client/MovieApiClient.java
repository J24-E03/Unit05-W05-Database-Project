package org.dci.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dci.domain.MovieApiResponse;
import org.dci.domain.MovieDetails;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieApiClient {
    private static final String API_KEY = System.getenv("MOVIE_API_KEY");
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    public MovieDetails getMovieDetails(String movieName) {
        try {
            String searchUrl = API_URL + "/search/movie?api_key="
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



}