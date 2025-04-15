package org.dci.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.dci.utils.ObjectMapperUtils;
import java.util.*;

@Data
public class HistoryRepository {
    private static final ObjectMapper objectMapper = ObjectMapperUtils.getInstance();
    private static HistoryRepository instance = null;

    public static HistoryRepository getInstance() {
        return Objects.requireNonNullElseGet(instance
                , () -> instance = new HistoryRepository());
    }

    private HistoryRepository() {
    }
}
