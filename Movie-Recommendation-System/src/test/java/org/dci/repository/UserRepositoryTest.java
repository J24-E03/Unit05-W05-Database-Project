package org.dci.repository;

import lombok.SneakyThrows;
import org.dci.domain.PasswordHashing;
import org.dci.domain.User;
import org.dci.domain.UserType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.net.URI;
import java.util.Optional;

import static org.dci.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;


class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    @SneakyThrows
    void setUp(){
        userRepository = UserRepository.getInstance();
    }

    @AfterEach
    public void deleteUsers() {

    }

    @Test
    public void testLoadingFile() {

    }

    @Test
    public void testSignUp() {
        Optional<User> newUserOptional = userRepository.signUp(SIGNUP_USERNAME1, PASSWORD, UserType.NORMAL_USER);
        if (newUserOptional.isPresent()) {
            User newUser = newUserOptional.get();
            assertNotNull(newUser);
            assertEquals(SIGNUP_USERNAME1, newUser.getUsername());
            assertEquals(UserType.NORMAL_USER, newUser.getType());
            assertNotEquals(PASSWORD, newUser.getPassword());
        }

    }


    @Test
    public void testLogin() {
        Optional<User> newUserOptional = userRepository.signUp(SIGNUP_USERNAME1, PASSWORD, UserType.NORMAL_USER);
        User logginUser = userRepository.login(SIGNUP_USERNAME1, PASSWORD);

        if (newUserOptional.isPresent()) {
            User newUser = newUserOptional.get();
            assertEquals(newUser, logginUser);
        }

    }

}