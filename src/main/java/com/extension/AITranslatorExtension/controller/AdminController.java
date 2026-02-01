package com.extension.AITranslatorExtension.controller;

import com.extension.AITranslatorExtension.service.RateLimitingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private RateLimitingService rateLimitingService;

    /**
     * Clear rate limiting buckets
     */
    @DeleteMapping("/rate-limit/clear")
    public ResponseEntity<Map<String, String>> clearRateLimits() {
        logger.info("Clearing rate limiting buckets");
        rateLimitingService.cleanup();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Rate limiting buckets cleared successfully");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        logger.info("Rate limiting buckets cleared successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get system health including rate limiting
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        logger.debug("System health check requested");
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("rateLimiting", "UP");
        return ResponseEntity.ok(health);
    }
}
