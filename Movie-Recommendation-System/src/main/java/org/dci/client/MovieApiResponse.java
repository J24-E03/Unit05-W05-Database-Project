package org.dci.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MovieApiResponse {
    @JsonProperty("page")
    private int page;
    @JsonProperty("results")
    private List<MovieDetails> results;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_results")
    private int totalResults;


}
