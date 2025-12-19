package com.ratelimiter.config;

import com.ratelimiter.core.TokenBucketRateLimiter;

import jakarta.servlet.Filter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 3 AutoConfiguration.
 */
@AutoConfiguration
@ConditionalOnClass(Filter.class)
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = "ratelimiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterAutoConfiguration {

    @Bean
    public TokenBucketRateLimiter tokenBucketRateLimiter() {
        return new TokenBucketRateLimiter();
    }

    @Bean
    public RateLimitingFilter rateLimitingFilter(TokenBucketRateLimiter limiter, RateLimiterProperties props) {
        return new RateLimitingFilter(limiter, props);
    }
}
