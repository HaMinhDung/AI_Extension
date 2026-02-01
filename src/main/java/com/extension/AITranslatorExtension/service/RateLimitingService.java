package com.extension.AITranslatorExtension.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.ip.requests:100}")
    private int ipRequestLimit;

    @Value("${rate.limit.ip.period:60}")
    private int ipPeriodSeconds;

    @Value("${rate.limit.endpoint.translate.requests:20}")
    private int translateRequestLimit;

    @Value("${rate.limit.endpoint.translate.period:60}")
    private int translatePeriodSeconds;

    @Value("${rate.limit.endpoint.health.requests:60}")
    private int healthRequestLimit;

    @Value("${rate.limit.endpoint.health.period:60}")
    private int healthPeriodSeconds;

    /**
     * Try to consume tokens for IP-based rate limiting
     * @param ipAddress IP address
     * @return true if request allowed, false if rate limited
     */
    public boolean tryConsumeForIp(String ipAddress) {
        String key = "ip:" + ipAddress;
        Bucket bucket = buckets.computeIfAbsent(key, k -> createIpBucket());
        boolean allowed = bucket.tryConsume(1);

        if (!allowed) {
            logger.warn("IP rate limit exceeded for: {}", ipAddress);
        }

        return allowed;
    }

    /**
     * Try to consume tokens for endpoint-specific rate limiting
     * @param endpoint API endpoint
     * @param identifier IP address or user identifier
     * @return true if request allowed, false if rate limited
     */
    public boolean tryConsumeForEndpoint(String endpoint, String identifier) {
        String key = "endpoint:" + endpoint + ":" + identifier;
        Bucket bucket = buckets.computeIfAbsent(key, k -> createEndpointBucket(endpoint));
        boolean allowed = bucket.tryConsume(1);

        if (!allowed) {
            logger.warn("Endpoint rate limit exceeded for {} on endpoint {}", identifier, endpoint);
        }

        return allowed;
    }

    /**
     * Create bucket for IP-based rate limiting
     */
    private Bucket createIpBucket() {
        logger.debug("Creating IP bucket with limit: {} requests per {} seconds", ipRequestLimit, ipPeriodSeconds);
        return Bucket.builder()
                .addLimit(Bandwidth.classic(ipRequestLimit,
                        Refill.intervally(ipRequestLimit, Duration.ofSeconds(ipPeriodSeconds))))
                .build();
    }

    /**
     * Create bucket for endpoint-specific rate limiting
     */
    private Bucket createEndpointBucket(String endpoint) {
        return switch (endpoint) {
            case "/api/generate" -> {
                logger.debug("Creating endpoint bucket for /api/generate with limit: {} requests per {} seconds",
                        translateRequestLimit, translatePeriodSeconds);
                yield Bucket.builder()
                        .addLimit(Bandwidth.classic(translateRequestLimit,
                                Refill.intervally(translateRequestLimit, Duration.ofSeconds(translatePeriodSeconds))))
                        .build();
            }
            case "/api/health" -> {
                logger.debug("Creating endpoint bucket for /api/health with limit: {} requests per {} seconds",
                        healthRequestLimit, healthPeriodSeconds);
                yield Bucket.builder()
                        .addLimit(Bandwidth.classic(healthRequestLimit,
                                Refill.intervally(healthRequestLimit, Duration.ofSeconds(healthPeriodSeconds))))
                        .build();
            }
            default -> {
                logger.debug("Creating default endpoint bucket with limit: 30 requests per 60 seconds");
                yield Bucket.builder()
                        .addLimit(Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1))))
                        .build();
            }
        };
    }

    /**
     * Get remaining tokens for a bucket
     */
    public long getRemainingTokens(String key) {
        Bucket bucket = buckets.get(key);
        return bucket != null ? bucket.getAvailableTokens() : 0;
    }

    /**
     * Clear expired buckets (optional cleanup method)
     */
    public void cleanup() {
        int size = buckets.size();
        buckets.clear();
        logger.info("Cleared {} rate limiting buckets", size);
    }
}
