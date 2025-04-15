package org.dci.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetails {
    @JsonProperty("original_title")
    private String title;
    @JsonProperty("id")
    private int id;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("overview")
    private String overview;
    @JsonProperty("genre_ids")
    private Set<Integer> genreIds;
    @JsonProperty("vote_average")
    private String rating;


    public LocalDate getReleaseDate() {
        return LocalDate.parse(releaseDate);
    }

    public double getRating() {
        return Double.parseDouble(rating);
    }

}
