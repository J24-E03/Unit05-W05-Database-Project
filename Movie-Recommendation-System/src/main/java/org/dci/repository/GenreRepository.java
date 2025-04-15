package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Genre;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GenreRepository {
    private static GenreRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(GenreRepository.class);
    public static final HikariDataSource dataSource = HikariCPConfig.getDataSource();

    public static GenreRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new GenreRepository());
    }

    private GenreRepository() {
    }

    public Set<Genre> getMovieGenres(Integer movieId) {
        Set<Genre> genres = new HashSet<>();
        List<Integer> genresIds = new ArrayList<>();
        String query = "SELECT * FROM movie_genres WHERE movie_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, movieId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Optional<Genre> genre = getGenre(resultSet.getInt("genre_id"));
                    genre.ifPresent(genres::add);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genres;
    }

    public Optional<Genre> getGenre(int genreId) {
        String query = """
                SELECT name FROM genres WHERE genre_id = ?
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, genreId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    Genre genre = new Genre();
                    genre.setName(name);
                    return Optional.of(genre);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Genre> addNewGenre(Connection connection, String genreName) {
        String query = """
                INSERT INTO genres(name)
                VALUES (?)
                ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
                RETURNING genre_id;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, genreName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int genreId = resultSet.getInt("genre_id");
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(genreName);
                    return Optional.of(genre);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}