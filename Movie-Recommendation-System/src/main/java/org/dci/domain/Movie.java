package org.dci.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class Movie {
    private Integer id;
    private String title;
    private LocalDate releaseDate;
    private String overview;
    private double rating;
    private Set<Genre> genres;
    private List<Actor> actors;

    private String formatLine(String text, int boxWidth) {
        int visibleTextLength = visibleLength(text);
        return "║ " + text + " ".repeat(boxWidth - visibleTextLength - 1) + "║";
    }

    private int visibleLength(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    public String makeMovieDetailsReadyForPrint() {
        final String BOLD = "\u001B[1m";
        final String ITALIC = "\u001B[3m";
        final String RESET_BOLD = "\u001B[22m";
        final String RESET_ITALIC = "\u001B[23m";
        final String INDENT = "  ";
        final String NEW_LINE = "\n";

        String titleLine = BOLD + "Title: " + RESET_BOLD + ITALIC + "\"" + title + "\"" + RESET_ITALIC;
        String releaseDateLine = BOLD + "Release Date: " + RESET_BOLD + ITALIC + releaseDate + RESET_ITALIC;
        String overviewLine = BOLD + "Overview: " + RESET_BOLD + ITALIC + overview + RESET_ITALIC;
        String ratingLine = BOLD + "Rating: " + RESET_BOLD + ITALIC + rating + RESET_ITALIC;


        int maxVisibleLength = Math.max(visibleLength(titleLine), Math.max(visibleLength(releaseDateLine),
                Math.max(visibleLength(overviewLine), visibleLength(ratingLine))));
        int boxWidth = maxVisibleLength + INDENT.length() + 2;


        String topBorder = "╔" + "═".repeat(boxWidth) + "╗";
        String bottomBorder = "╚" + "═".repeat(boxWidth) + "╝";

        return topBorder + NEW_LINE +
                formatLine(titleLine, boxWidth) + NEW_LINE +
                formatLine(INDENT + ratingLine, boxWidth) + NEW_LINE +
                formatLine(INDENT + releaseDateLine, boxWidth) + NEW_LINE+
                formatLine(INDENT + overviewLine, boxWidth) + NEW_LINE +
                bottomBorder;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", genres=" + genres +
                ", actors=" + actors +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Movie movie)) return false;

        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
