package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Genre;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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
            logger.error("Could not get genres for movieID: {}", movieId, e);
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
                    genre.setId(genreId);
                    return Optional.of(genre);
                }
            }
        } catch (SQLException e) {
            logger.error("Could not get genre for genreID: {}", genreId, e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void addNewGenre(Genre genre) {
        if (isGenreExist(genre.getId(), genre.getName())) {
            return;
        }
        String query = """
                INSERT INTO genres(genre_id, name)
                VALUES (?,?)
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, genre.getId());
            preparedStatement.setString(2, genre.getName());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to add genre");
            }

        } catch (SQLException e) {
            logger.error("Could not add genre: {}", genre, e);
            throw new RuntimeException(e);
        }
    }

    public boolean isGenresEmpty() {
        String query = "SELECT COUNT(*) AS genres_count FROM genres";
        int genresCount = 0;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    genresCount = resultSet.getInt("genres_count");
                }
            }

        } catch (SQLException e) {
            logger.error("Could not get genres count", e);
            throw new RuntimeException(e);
        }
        return genresCount == 0;
    }

    public boolean isGenreExist(Integer genreId, String name) {
        String query = """
                SELECT COUNT(*) AS genres_count FROM genres WHERE genre_id = ? AND name = ?
                """;

        int count = 0;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, genreId);
            preparedStatement.setString(2, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("genres_count");
                }
            }

        } catch (SQLException e) {
            logger.error("Could not get genre with genreID: {}", genreId, e);
            throw new RuntimeException(e);
        }

        return count == 1;
    }


}