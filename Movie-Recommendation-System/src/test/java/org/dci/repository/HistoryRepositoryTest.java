package org.dci.repository;

import lombok.SneakyThrows;
import org.dci.domain.HistoryInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.dci.TestFixtures.*;

class HistoryRepositoryTest {

    private HistoryRepository historyRepository;

    @BeforeEach
    @SneakyThrows
    void setUp() throws URISyntaxException {
        URI resourcesFolder = getClass().getClassLoader().getResource("").toURI();
        historyRepository = HistoryRepository.getInstance(Path.of(resourcesFolder));
    }

    @AfterEach
    void tearDown() {
        historyRepository.clear();
    }

    @Test
    void testAddHistory() {
        LocalDateTime timestamp = LocalDateTime.now();
        historyRepository.addUserHistory(USER_ID, QUERY, MOVIE_LIST, timestamp);

        List<HistoryInfo> userHistoryInfo = historyRepository.getUserHistory(USER_ID);
        assertNotNull(userHistoryInfo);
        assertEquals(1, userHistoryInfo.size());
        HistoryInfo historyInfo = userHistoryInfo.get(0);
        assertEquals(QUERY, historyInfo.getQuery());
        assertEquals(MOVIE_LIST, historyInfo.getMovies());
    }

    @Test
    void testGetUserHistory_UserExists() {
        LocalDateTime timestamp = LocalDateTime.now();

        historyRepository.addUserHistory(USER_ID, QUERY, MOVIE_LIST, timestamp);


        List<HistoryInfo> userHistoryInfo = historyRepository.getUserHistory(USER_ID);

        assertNotNull(userHistoryInfo);
        assertFalse(userHistoryInfo.isEmpty());
    }



}