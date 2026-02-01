package com.extension.AITranslatorExtension.controller;

import com.extension.AITranslatorExtension.dto.TranslateRequest;
import com.extension.AITranslatorExtension.dto.TranslateResponse;
import com.extension.AITranslatorExtension.service.TranslationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TranslateController {

    private static final Logger logger = LoggerFactory.getLogger(TranslateController.class);
    private final TranslationService translationService;

    public TranslateController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TranslateResponse> generate(@Valid @RequestBody TranslateRequest request) {
        logger.info("Received generate request with prompt: {}",
                request.getPrompt() != null ? request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())) : "null");

        try {
            TranslateResponse response = translationService.translate(request);

            if (response.isSuccess()) {
                logger.info("Successfully generated response");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Generate request failed: {}", response.getError());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing generate request: {}", e.getMessage(), e);
            TranslateResponse errorResponse = TranslateResponse.error("Server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("Health check request received");
        return ResponseEntity.ok("Service is running");
    }
}
