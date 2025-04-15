package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Movie;
import org.dci.domain.Query;
import org.dci.domain.User;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class QueryRepository {
    private static QueryRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(QueryRepository.class);
    public static final HikariDataSource dataSource = HikariCPConfig.getDataSource();
    private final MovieRepository movieRepository = MovieRepository.getInstance();

    public static QueryRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new QueryRepository());
    }

    private QueryRepository () {
    }

    public Optional<Query> addNewQuery(int userId, String userInput, List<Movie> movies) {
        String sqlQuery = """
                INSERT INTO queries (query, user_id, timestamp) VALUES (?, ?, ?)
                RETURNING query_id;
                """;

        Integer queryId = null;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            LocalDateTime now = LocalDateTime.now();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);) {
                preparedStatement.setString(1, userInput);
                preparedStatement.setInt(2, userId);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(now));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        queryId = resultSet.getInt("query_id");
                        int finalQueryId = queryId;
                        movies.forEach(movie -> {addNewQueryMovieRelation(connection, finalQueryId, movie.getId());
                        });
                    }
                }
            }
            connection.commit();
            if (queryId != null) {
                return Optional.of(new Query(queryId, userId, userInput, now, movies));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNewQueryMovieRelation(Connection connection, int queryId, int movieId) {
        String sqlQuery = """
                INSERT INTO query_movies (query_id, movie_id) VALUES (?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, queryId);
            preparedStatement.setInt(2, movieId);

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Could not add movie to query movies");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Query> viewAllUserHistories(User loggedUser) {
        String sqlQuery = """
                SELECT q.query_id, q.query, q.timestamp, qm.movie_id
                FROM queries q
                JOIN query_movies qm ON qm.query_id = q.query_id
                WHERE q.user_id = ?
                """;

        Map<Query,List<Movie>> queryMoviesMap = new HashMap<>();
        try(Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, loggedUser.getId());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Query movieQuery = new Query(resultSet.getInt("query_id"),
                                loggedUser.getId(),
                                resultSet.getString("query"),
                                resultSet.getTimestamp("timestamp").toLocalDateTime());

                        queryMoviesMap.putIfAbsent(movieQuery, new ArrayList<>());
                        Optional<Movie> movieOptional = movieRepository.getMovieById(connection, resultSet.getInt("movie_id"));
                        movieOptional.ifPresent(movie -> queryMoviesMap.get(movieQuery).add(movie));

                    }
                }
            }
            queryMoviesMap.forEach((query, movies) -> query.setMovies(movies));
            return queryMoviesMap.keySet().stream().toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Query> viewUserHistoryInRange (User loggedUser, LocalDateTime from, LocalDateTime to) {
        String sqlQuery = """
                SELECT q.query_id, q.query, q.timestamp, qm.movie_id
                FROM queries q
                JOIN query_movies qm ON qm.query_id = q.query_id
                WHERE q.user_id = ? AND q.timestamp >= ? AND q.timestamp <= ?
                """;
        Map<Query,List<Movie>> queryMoviesMap = new HashMap<>();
        try(Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, loggedUser.getId());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(from));
                preparedStatement.setTimestamp(3, Timestamp.valueOf(to));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Query movieQuery = new Query(resultSet.getInt("query_id"),
                                loggedUser.getId(),
                                resultSet.getString("query"),
                                resultSet.getTimestamp("timestamp").toLocalDateTime());

                        queryMoviesMap.putIfAbsent(movieQuery, new ArrayList<>());
                        Optional<Movie> movieOptional = movieRepository.getMovieById(connection, resultSet.getInt("movie_id"));
                        movieOptional.ifPresent(movie -> queryMoviesMap.get(movieQuery).add(movie));

                    }
                }
            }
            queryMoviesMap.forEach((query, movies) -> query.setMovies(movies));
            return queryMoviesMap.keySet().stream().toList();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    }
}
