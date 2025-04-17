package org.dci.menu;

import org.dci.domain.NormalUser;
import org.dci.domain.User;
import org.dci.domain.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;

import java.net.URISyntaxException;


import static org.junit.jupiter.api.Assertions.*;


class LoggedInUserMenuTest {
    private User mockUser;
    private LoggedInUserMenu menu;


    @BeforeEach
    void setup() throws URISyntaxException {
        mockUser = new NormalUser();
        mockUser.setUsername("testuser");
        mockUser.setType(UserType.NORMAL_USER);
        mockUser.setId(1);
        MockitoAnnotations.openMocks(this);
        menu = new LoggedInUserMenu(mockUser);
    }

    @Test
    void testConstructorInitializesCorrectly() {
        assertEquals("Welcome testuser", menu.getTitle());
        assertNotNull(menu.getActions());
        assertFalse(menu.getActions().isEmpty());
    }

}