package org.dci.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Genre {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
