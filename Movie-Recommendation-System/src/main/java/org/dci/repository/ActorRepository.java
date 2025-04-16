package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Actor;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class ActorRepository {
    private static ActorRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ActorRepository.class);
    public static final HikariDataSource dataSource = HikariCPConfig.getDataSource();

    public static ActorRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new ActorRepository());
    }

    private ActorRepository() {
    }

    public Optional<Actor> addNewActor(Actor actor) {
        if (actorExist(actor)) {
            return Optional.of(actor);
        }
        String query = """
                INSERT INTO actors (actor_id, name) VALUES (?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, actor.getId());
            preparedStatement.setString(2, actor.getName());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(actor);
            }

        } catch (SQLException e) {
            logger.error("Could not add new actor: {}", actor.getId(), e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean actorExist(Actor actor) {
        String query = """
                SELECT COUNT(*) AS counter FROM actors WHERE actor_id = ? AND name = ?
                """;
        int counter = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, actor.getId());
            preparedStatement.setString(2, actor.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                   counter = resultSet.getInt("counter");
                }
            }

        } catch (SQLException e) {
            logger.error("Could not get actor with actorID: {}", actor.getId(), e);
            throw new RuntimeException(e);
        }

        return counter > 0;
    }

}
