package com.extension.AITranslatorExtension.client;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeminiClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    public String sendRequest(String apiKey, String prompt) throws Exception {
        logger.debug("Sending request to Gemini API");

        // Tạo client với API key
        Client client = new Client(apiKey);

        // Gọi API với prompt nguyên bản từ frontend
        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash-lite",
            prompt,
            null
        );

        logger.debug("Received response from Gemini API");

        // Trả về response từ Gemini
        return response.text();
    }
}