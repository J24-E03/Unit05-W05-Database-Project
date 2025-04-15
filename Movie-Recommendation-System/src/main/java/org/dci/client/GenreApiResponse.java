package org.dci.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dci.domain.Genre;

import java.util.List;

@Data
public class GenreApiResponse {
    @JsonProperty("genres")
    private List<Genre> genres;
}