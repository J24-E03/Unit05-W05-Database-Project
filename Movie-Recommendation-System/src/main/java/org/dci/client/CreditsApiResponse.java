package org.dci.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dci.domain.Actor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsApiResponse {
    @JsonProperty("cast")
    private List<Actor> actors;
}
