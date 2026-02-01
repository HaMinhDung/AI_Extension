package com.extension.AITranslatorExtension.filter;

import com.extension.AITranslatorExtension.service.RateLimitingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    @Autowired
    private RateLimitingService rateLimitingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Only apply rate limiting to API endpoints
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String ipAddress = getClientIpAddress(httpRequest);
        String endpoint = requestURI;

        logger.debug("Rate limiting check for IP: {} on endpoint: {}", ipAddress, endpoint);

        // Check IP-based rate limit (global protection)
        if (!rateLimitingService.tryConsumeForIp(ipAddress)) {
            logger.warn("IP rate limit exceeded for: {}", ipAddress);
            sendRateLimitResponse(httpResponse, "IP rate limit exceeded. Please try again later.",
                                rateLimitingService.getRemainingTokens("ip:" + ipAddress));
            return;
        }

        // Check endpoint-specific rate limit
        if (!rateLimitingService.tryConsumeForEndpoint(endpoint, ipAddress)) {
            logger.warn("Endpoint rate limit exceeded for IP: {} on endpoint: {}", ipAddress, endpoint);
            sendRateLimitResponse(httpResponse, "Endpoint rate limit exceeded. Please slow down.",
                                rateLimitingService.getRemainingTokens("endpoint:" + endpoint + ":" + ipAddress));
            return;
        }

        // Add rate limit headers to response
        addRateLimitHeaders(httpResponse, endpoint, ipAddress);

        chain.doFilter(request, response);
    }

    /**
     * Get client IP address, considering X-Forwarded-For header
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Send rate limit exceeded response
     */
    private void sendRateLimitResponse(HttpServletResponse response, String message, long remainingTokens)
            throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000)); // Reset in 1 minute

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", 429);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("remainingRequests", remainingTokens);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        logger.info("Rate limit response sent with {} remaining tokens", remainingTokens);
    }

    /**
     * Add rate limiting information to response headers
     */
    private void addRateLimitHeaders(HttpServletResponse response, String endpoint, String ipAddress) {
        long ipRemaining = rateLimitingService.getRemainingTokens("ip:" + ipAddress);
        long endpointRemaining = rateLimitingService.getRemainingTokens("endpoint:" + endpoint + ":" + ipAddress);

        response.setHeader("X-RateLimit-IP-Remaining", String.valueOf(ipRemaining));
        response.setHeader("X-RateLimit-Endpoint-Remaining", String.valueOf(endpointRemaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Rate limiting filter initialized");
    }

    @Override
    public void destroy() {
        logger.info("Rate limiting filter destroyed");
    }
}
