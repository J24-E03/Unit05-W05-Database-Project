package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Actor;
import org.dci.domain.Genre;
import org.dci.domain.Movie;
import org.dci.client.MovieDetails;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
    public Optional<Movie> addNewMovie(MovieDetails movieDetails, List<Actor> actors) {
        String query = """
                INSERT INTO movies (title, release_date, overview, rating)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (title, release_date) DO UPDATE SET title = EXCLUDED.title
                RETURNING movie_id;
                """;

        Integer movieId = null;
        try (Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);

            Set<Genre> movieGenres = new HashSet<>();
            try(PreparedStatement preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setString(1, movieDetails.getTitle());
                preparedStatement.setDate(2, Date.valueOf(movieDetails.getReleaseDate()));
                preparedStatement.setString(3, movieDetails.getOverview());
                preparedStatement.setDouble(4, movieDetails.getRating());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        movieId = resultSet.getInt("movie_id");
                        int movieIdFinal = movieId;
                        actors.forEach(actor -> {addMovieActorRelation(connection, movieIdFinal, actor.getId());});
                        movieDetails.getGenreIds().forEach(genreId -> {
                            Optional<Genre> genre = genreRepository.getGenre(genreId);
                            if (genre.isPresent()) {
                                Genre genreFinal = genre.get();
                                if (!genreMovieRelationExists(connection, movieIdFinal, genreFinal.getId())) {
                                    addNewMovieGenreRelation(connection, movieIdFinal, genreFinal.getId());
                                }
                                movieGenres.add(genre.get());


                            }

                        });
                    }
                }
                connection.commit();
                if (movieId != null) {
                    Movie movie = new Movie();
                    movie.setGenres(movieGenres);
                    movie.setId(movieId);
                    movie.setTitle(movieDetails.getTitle());
                    movie.setOverview(movieDetails.getOverview());
                    movie.setReleaseDate(movieDetails.getReleaseDate());
                    movie.setRating(movieDetails.getRating());
                    movie.setActors(actors);
                    return Optional.of(movie);
                } else {
                    return Optional.empty();
                }

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

    private boolean genreMovieRelationExists(Connection connection, int movieId, int genreId) {
        String query = """
                SELECT count(*) AS counter FROM movie_genres WHERE movie_id = ? AND genre_id = ?
                """;

        int counter = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, genreId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    counter = resultSet.getInt("counter");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return counter == 1;
    }

    public Optional<Movie> getMovieById(Connection connection, Integer movieId) {
        String query = """
                SELECT m.*, mg.genre_id
                FROM movies m
                JOIN movie_genres mg ON m.movie_id = mg.movie_id
                WHERE m.movie_id = ?
                """;

        Movie movie = null;
        Set<Genre> genres = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, movieId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
               while (resultSet.next()) {
                    if (movie == null) {
                        movie = new Movie();
                        movie.setId(movieId);
                        movie.setTitle(resultSet.getString("title"));
                        movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                        movie.setOverview(resultSet.getString("overview"));
                        movie.setRating(resultSet.getDouble("rating"));
                    }

                    Optional<Genre> genreOptional = genreRepository.getGenre(resultSet.getInt("genre_id"));
                    genreOptional.ifPresent(genres::add);
                }
            }
            if (movie != null) {
                movie.setGenres(genres);
                return Optional.of(movie);
            } else {
                return Optional.empty();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMovieActorRelation(Connection connection, Integer movieId, Integer actorId) {
        String query = """
                INSERT INTO movie_actors (movie_id, actor_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, actorId);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("Could not add movie actor relationship:");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}