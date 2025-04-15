package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Genre;
import org.dci.domain.Movie;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class MovieRepository {
    private static MovieRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(MovieRepository.class);
    private static final HikariDataSource dataSource = HikariCPConfig.getDataSource();
    private static final GenreRepository genreRepository = GenreRepository.getInstance();

    public static MovieRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new MovieRepository());
    }

    private MovieRepository() {
    }

    public Optional<Movie> findMovieByTitleAndDate(String title, LocalDate releaseDate) {
        String query = """
                SELECT * FROM movies
                         WHERE title = ? AND release_date = ?
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, title);
            preparedStatement.setDate(2, Date.valueOf(releaseDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Movie movie = new Movie();
                    movie.setId(resultSet.getInt("movie_id"));
                    movie.setTitle(resultSet.getString("title"));
                    movie.setOverview(resultSet.getString("overview"));
                    movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                    movie.setRating(resultSet.getDouble("rating"));
                    movie.setGenres(genreRepository.getMovieGenres(movie.getId()));
                    return Optional.of(movie);
                }
            }


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }
    public void addNewMovie(Movie movie) {
        String query = """
                INSERT INTO movies (title, release_date, overview, rating)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (title, release_date) DO UPDATE SET title = EXCLUDED.title
                RETURNING movie_id;
                """;

        try (Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);

            try(PreparedStatement preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setString(1, movie.getTitle());
                preparedStatement.setDate(2, Date.valueOf(movie.getReleaseDate()));
                preparedStatement.setString(3, movie.getOverview());
                preparedStatement.setDouble(4, movie.getRating());


                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int movieId = resultSet.getInt("movie_id");
                        movie.getGenres().forEach(genre -> {
                            Optional<Genre> genreOptional = genreRepository.addNewGenre(connection, genre.getName());
                            if (genreOptional.isEmpty()) {
                                throw new RuntimeException("Could not add genre: " + genre.getName());
                            }
                            addNewMovieGenreRelation(connection, movieId, genreOptional.get().getId());
                        });
                        movie.setId(resultSet.getInt("movie_id"));
                    }
                }
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewMovieGenreRelation(Connection connection, int movieId, int genreId) {
        String query = """
                INSERT INTO movie_genres (movie_id, genre_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, genreId);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("Could not add genre: " + genreId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}