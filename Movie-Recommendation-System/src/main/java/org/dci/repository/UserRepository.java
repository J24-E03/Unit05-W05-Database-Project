package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.dci.domain.*;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Data
public class UserRepository {
    private static UserRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    public static final HikariDataSource dataSource = HikariCPConfig.getDataSource();

    public static UserRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new UserRepository());
    }

    private UserRepository () {
    }

    public Optional<User> getUserByUserName(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user;
                    int id = resultSet.getInt("user_id");
                    String password = resultSet.getString("password");
                    String type = resultSet.getString("type");
                    if (type.equals("NORMAL_USER")) {
                        user = new NormalUser(id, username, password, UserType.NORMAL_USER);
                    } else {
                        user = new NormalUser(id, username, password, UserType.PREMIUM_USER);
                    }
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Could not get user: {}", username, e);
            throw new RuntimeException("Could not get user", e);
        }
        return Optional.empty();
    }

    public void validateUsername(String username) {
        if (getUserByUserName(username).isPresent()) {
            throw new IllegalArgumentException("Username is already exists");
        }
        if (username == null || username.isBlank() || !username.matches("^[^A-Z]*$")) {
            throw new IllegalArgumentException("Username is not valid. It shouldn't contain any Uppercase letters.");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit.");
        }
    }

    public Optional<User> signUp(String username, String password, UserType userType, String fullName, LocalDate birthDate, String email) {
        String query = """
                INSERT INTO users (username, password, type)
                VALUES (?, ?, ?)
                RETURNING user_id
                """;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, PasswordHashing.hashPasswordSHA1(password));
                preparedStatement.setString(3, userType.name());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("user_id");
                        Optional<UserDetails> userDetails = addNewUserDetails(connection, userId, fullName, birthDate, email);
                        if (userDetails.isPresent()) {
                            connection.commit();
                            if (userType == UserType.NORMAL_USER) {
                                return Optional.of(new NormalUser(userId, username, password, UserType.NORMAL_USER));
                            } else {
                                return Optional.of(new NormalUser(userId, username, password, UserType.PREMIUM_USER));
                            }
                        } else {
                            connection.rollback();
                        }
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Could not sign up: {}", username, e);
                throw new RuntimeException("Error during user sign-up: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            logger.error("Could not sign up: {}", username, e);
            throw new RuntimeException("Error getting database connection: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

        private Optional<UserDetails> addNewUserDetails(Connection connection, int userId, String fullName, LocalDate birthDate, String email) {
        String query = """
                INSERT INTO user_details (user_id, full_name, email, date_of_birth)
                VALUES (?, ?, ?, ?)
                RETURNING user_id
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, email);
            preparedStatement.setDate(4, Date.valueOf(birthDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new UserDetails(resultSet.getInt("user_id"), fullName, birthDate, email));
                }
            }

        } catch (SQLException e) {
            logger.error("Could not add new user details", e);
            throw new RuntimeException("Error inserting user details: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public User login(String username, String password) {
        Optional<User> userOptional = getUserByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.matchPassword(password)) {
                return user;
            }
            throw new IllegalArgumentException("Login failed.");
        }
        throw new IllegalArgumentException("username doesn't exist.");
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be empty.");
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email address is invalid.");
        }
    }

    public LocalDate validateDateOfBirth(String birthDateString) {
        if (birthDateString == null || birthDateString.isBlank()) {
            throw new IllegalArgumentException("Date of Birth cannot be empty.");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthDateString, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }

        Period age = Period.between(birthDate, LocalDate.now());

        if (age.getYears() < 18) {
            throw new IllegalArgumentException("User must be at least 18 years old.");
        }
        return birthDate;
    }

    public boolean upgradeUser(User user) {
        String query = """
                UPDATE users SET type = 'PREMIUM_USER' WHERE user_id = ?
                """;

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, user.getId());

            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            logger.error("Could not upgrade user: {}", user.getUsername(), e);
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDetails> viewUserInfo(User user) {
        String query = "SELECT * FROM get_user_profile(?)";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, user.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new UserDetails(
                            user.getId(),
                            resultSet.getString("full_name"),
                            resultSet.getDate("date_of_birth").toLocalDate(),
                            resultSet.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Could not retrieve user details: {}", user.getUsername(), e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean updateUser(UserDetails userDetails) {
        String query = """
                UPDATE user_details SET full_name = ?, date_of_birth = ?, email = ?
                WHERE user_id = ?
                """;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, userDetails.getFullName());
            preparedStatement.setDate(2, Date.valueOf(userDetails.getBirthDate()));
            preparedStatement.setString(3, userDetails.getEmail());
            preparedStatement.setInt(4, userDetails.getUser_id());

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            logger.error("Could not update user details for userID: {}", userDetails.getUser_id(), e);
            throw new RuntimeException(e);
        }

    }
}
