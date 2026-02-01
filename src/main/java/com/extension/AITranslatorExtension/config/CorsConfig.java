package com.extension.AITranslatorExtension.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Browser Extensions and Web Clients
 * Allows all origins including chrome-extension://, moz-extension://, etc.
 */
@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsFilter corsFilter() {
        logger.info("Initializing CORS configuration for browser extensions and web clients");

        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (including chrome-extension://, moz-extension://, etc.)
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // Allow all headers (including custom headers from extensions)
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Expose all headers to the client
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-RateLimit-IP-Remaining",
            "X-RateLimit-Endpoint-Remaining",
            "X-RateLimit-Reset"
        ));

        // Cache preflight requests for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        logger.info("CORS configuration completed - allowing all origins with credentials");

        return new CorsFilter(source);
    }
}
