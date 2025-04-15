package org.dci.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Query {
    private Integer queryId;
    private Integer userId;
    private String query;
    private LocalDateTime timestamp;
    private List<Movie> movies;

    public Query(Integer queryId, Integer userId, String query, LocalDateTime timestamp) {
        this.queryId = queryId;
        this.userId = userId;
        this.query = query;
        this.timestamp = timestamp;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Query query)) return false;

        return queryId.equals(query.queryId);
    }

    @Override
    public int hashCode() {
        return queryId.hashCode();
    }

    public String prepareHistoryTobeShown() {
        final String BOLD = "\u001B[1m";
        final String ITALIC = "\u001B[3m";
        final String RESET_BOLD = "\u001B[22m";
        final String RESET_ITALIC = "\u001B[23m";
        final String NEW_LINE = "\n";

        return BOLD + "Timestamp: " + RESET_BOLD + ITALIC + timestamp + RESET_ITALIC + NEW_LINE +
               BOLD +  "Your query: \"" + RESET_BOLD + ITALIC + query + '\"' + RESET_ITALIC + NEW_LINE +
                BOLD + "Recommendation: " + RESET_BOLD + NEW_LINE +
               String.join(NEW_LINE ,movies.stream().map(Movie::makeMovieDetailsReadyForPrint).toList());
    }
}
