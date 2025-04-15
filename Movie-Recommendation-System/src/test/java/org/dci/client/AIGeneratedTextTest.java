package org.dci.client;

import org.dci.domain.AIGeneratedText;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AIGeneratedTextTest {
    private static final String INSTRUCTION = "List exactly and only 5 movie names, numbered in this format: \n" +
            "1. Movie Name  \n" +
            "2. Movie Name  \n" +
            "3. Movie Name  \n" +
            "4. Movie Name  \n" +
            "5. Movie Name  \n" +
            "Do not include any introductions, explanations, or extra words. Only output the five numbered movie names in this format." +
            "Give me short answer. Just the numbered Movie names with no extra thing else.";;

    @Test
    void testParseAIGeneratedText() {
        String userInput = "I love sci-fi and adventure movies. Can you recommend some?";
        Set<String> movieNames = new HashSet<>(Arrays.asList("Interstellar", "The Martian",  "Inception", "Gravity", "Blade Runner 2049"));
        AIGeneratedText aiGeneratedText = new AIGeneratedText(userInput+INSTRUCTION+"1. Interstellar\n" +
                "2. The Martian\n" +
                "3. Inception\n" +
                "4. Gravity\n" +
                "5. Blade Runner 2049");

        assertEquals(movieNames, aiGeneratedText.parseAIGeneratedText(userInput+INSTRUCTION));

    }

}