package org.dci.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariCPConfig {
    private static final Logger logger = LoggerFactory.getLogger(HikariCPConfig.class);
    private static HikariDataSource dataSource;


    public static void initialize(String databaseName) {
        String baseUrl = System.getenv("DB_URL");
        String jdbcUrl = baseUrl + databaseName;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(System.getenv("DB_USER"));
        config.setPassword(System.getenv("DB_PASSWORD"));
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
        logger.info("HikariCP initialized with DB: {}", databaseName);
    }

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("HikariCPConfig is not initialized.");
        }
        return dataSource;
    }
}

