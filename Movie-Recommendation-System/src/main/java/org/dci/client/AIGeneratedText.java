package org.dci.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class AIGeneratedText {
    @JsonProperty("generated_text")
    private String generatedText;

    public AIGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    public Set<String> parseAIGeneratedText(String input) {
        String movieListString = generatedText.replace(input, "")
                .trim().replaceAll("\"", "");


        String[] movieNames = movieListString.split("\n");
        Set<String> movieNameList = new HashSet<>();

        for (String s : movieNames) {
            if (s.matches("\\d+\\.\\s+.*")) {
                movieNameList.add(s.replaceAll("\\d+\\.\\s+", "").trim());
            }
        }

        return movieNameList;
    }

}