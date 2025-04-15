package org.dci.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    @JsonProperty("id")
    private int id;

    @JsonProperty("userId")
    private int userId;
}
