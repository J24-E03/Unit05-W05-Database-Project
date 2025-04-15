package org.dci.client;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dci.domain.AIGeneratedText;
import org.dci.domain.AIQuery;
import org.dci.utils.Logger;
import org.dci.utils.ObjectMapperUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HuggingFaceApiClient {

    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3";
    private static final String API_TOKEN = System.getenv("AI_API_KEY");
    private static final ObjectMapper objectMapper = ObjectMapperUtils.getInstance();

    private static final String INSTRUCTION =
"""
        List exactly and only {{number}} movie names, numbered in this format:
        1. Movie Name
        2. Movie Name
        3. Movie Name
        ...
        Do not include any introductions, explanations, or extra words. Only output the movie names.Give me short answer. Just the numbered Movie names with no extra thing else.
""";

    public Set<String> getMovieRecommendation(String userInput, int movieNumbers) {
        try {
            String prompt = userInput + "\n--------\n" + INSTRUCTION.replace("{{number}}",
                    Integer.toString(movieNumbers));
            AIGeneratedText recommendations = getMovieRecommendations(prompt);
            return recommendations.parseAIGeneratedText(prompt);
        } catch (IOException e) {
            Logger.error("An Error happened: " + e.getMessage());
        }
        return new HashSet<>();
    }

    public static AIGeneratedText getMovieRecommendations(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        AIQuery query = new AIQuery(prompt);
        String jsonInput = objectMapper.writeValueAsString(query);

        RequestBody body = RequestBody.create(jsonInput, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_TOKEN)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        AIGeneratedText[] generatedTexts = objectMapper.readerForArrayOf(AIGeneratedText.class).readValue(responseBody);
        return generatedTexts[0];
    }
}
