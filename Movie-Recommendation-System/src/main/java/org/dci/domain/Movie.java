package org.dci.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Movie {
    private Integer id;
    private String title;
    private LocalDate releaseDate;
    private String overview;
    private double rating;
    private Set<Genre> genres;
}
