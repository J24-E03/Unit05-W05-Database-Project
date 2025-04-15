package org.dci.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.dci.domain.Query;
import org.dci.utils.HikariCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class QueryRepository {
    private static QueryRepository instance = null;
    private static final Logger logger = LoggerFactory.getLogger(QueryRepository.class);
    public static final HikariDataSource dataSource = HikariCPConfig.getDataSource();

    public static QueryRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new QueryRepository());
    }

    private QueryRepository () {
    }
}
