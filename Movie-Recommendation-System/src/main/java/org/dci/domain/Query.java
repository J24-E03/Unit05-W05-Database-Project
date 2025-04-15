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
}
