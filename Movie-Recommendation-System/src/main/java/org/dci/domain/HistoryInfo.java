package org.dci.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dci.client.MovieDetails;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryInfo {
    @JsonProperty("id")
    private int id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("query")
    private String query;

    @JsonProperty("movies")
    private List<MovieDetails> movies;

    @Override
    public String toString() {
        return "Timestamp: " + timestamp  +
                "Your query: \"" + query + '\"' +
                "Recommendation: \"" + movies + '\"';
    }

//    public String prepareHistoryTobeShown() {
//        final String BOLD = "\u001B[1m";
//        final String ITALIC = "\u001B[3m";
//        final String RESET_BOLD = "\u001B[22m";
//        final String RESET_ITALIC = "\u001B[23m";
//        final String NEW_LINE = "\n";
//
//        return BOLD + "Timestamp: " + RESET_BOLD + ITALIC + timestamp + RESET_ITALIC + NEW_LINE +
//                BOLD +  "Your query: \"" + RESET_BOLD + ITALIC + query + '\"' + RESET_ITALIC + NEW_LINE +
//                BOLD + "Recommendation: " + RESET_BOLD + NEW_LINE +
//                String.join(NEW_LINE ,movies.stream().map(MovieDetails::makeMovieDetailsReadyForPrint).toList());
//    }
}
