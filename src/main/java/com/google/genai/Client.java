package com.google.genai;

import com.google.genai.types.GenerateContentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String apiKey;
    public final Models models;

    public Client() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        this.models = new Models(this.apiKey);
    }

    public Client(String apiKey) {
        this.apiKey = apiKey;
        this.models = new Models(apiKey);
    }

    public static class Models {
        private static final Logger logger = LoggerFactory.getLogger(Models.class);
        private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
        private final String apiKey;
        private final HttpClient httpClient;
        private final ObjectMapper objectMapper;

        public Models(String apiKey) {
            this.apiKey = apiKey;
            this.httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            this.objectMapper = new ObjectMapper();
        }

        public GenerateContentResponse generateContent(String model, String prompt, Object config) {
            try {
                String requestBody = String.format("""
                    {
                        "contents": [{
                            "parts": [{
                                "text": %s
                            }]
                        }]
                    }
                    """, objectMapper.writeValueAsString(prompt));

                logger.debug("Sending request to: {}", GEMINI_API_URL + model + ":generateContent");
                logger.trace("Request body: {}", requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GEMINI_API_URL + model + ":generateContent?key=" + apiKey))
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(30))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                logger.debug("Response status: {}", response.statusCode());
                logger.trace("Response body: {}", response.body());

                if (response.statusCode() != 200) {
                    logger.error("Gemini API error: {}", response.body());
                    throw new RuntimeException("Gemini API error: " + response.body());
                }

                return parseGeminiResponse(response.body());
            } catch (Exception e) {
                logger.error("Error in generateContent: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to generate content", e);
            }
        }

        private GenerateContentResponse parseGeminiResponse(String responseBody) throws Exception {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (candidates.isEmpty()) {
                throw new RuntimeException("No result from Gemini");
            }

            JsonNode content = candidates.get(0).path("content").path("parts");
            if (content.isEmpty()) {
                throw new RuntimeException("Empty result");
            }

            String text = content.get(0).path("text").asText();
            return new GenerateContentResponse(text);
        }
    }
}
