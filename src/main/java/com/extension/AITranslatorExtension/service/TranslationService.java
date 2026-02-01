package com.extension.AITranslatorExtension.service;

import com.extension.AITranslatorExtension.client.GeminiClient;
import com.extension.AITranslatorExtension.dto.TranslateRequest;
import com.extension.AITranslatorExtension.dto.TranslateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final GeminiClient geminiClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public TranslationService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public TranslateResponse translate(TranslateRequest request) {
        try {
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                logger.warn("Translation request received with empty text");
                return TranslateResponse.error("Text cannot be empty");
            }

            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                logger.warn("Translation request received with empty prompt");
                return TranslateResponse.error("Prompt cannot be empty");
            }

            logger.debug("Processing translation request with prompt: {}",
                    request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())));

            // Build a prompt that asks the model to respond concisely in a single short paragraph
            String fullPrompt = String.format(
                "%s\n\nText: \"%s\"\n\nAnswer as ONE short paragraph, concise and to the point. Avoid filler.",
                request.getPrompt(),
                request.getText()
            );

            // Sử dụng API key từ application.properties
            String result = geminiClient.sendRequest(apiKey, fullPrompt);

            logger.info("Translation completed successfully");
            return TranslateResponse.success(result, false); // fromCache always false now

        } catch (Exception e) {
            logger.error("Translation failed: {}", e.getMessage(), e);
            return TranslateResponse.error("Translation failed: " + e.getMessage());
        }
    }
}
