package com.ratelimiter.config;

import com.ratelimiter.api.RateLimiter;
import com.ratelimiter.api.RateLimitKeyResolver;
import com.ratelimiter.api.RateLimitPolicyResolver;
import com.ratelimiter.core.InMemoryTokenBucketStore;
import com.ratelimiter.core.TokenBucketRateLimiter;
import com.ratelimiter.core.TokenBucketStore;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/** AutoConfiguration*/
@AutoConfiguration
@ConditionalOnClass(Filter.class)
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = "ratelimiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TokenBucketStore.class)
    @ConditionalOnProperty(prefix = "ratelimiter", name = "mode", havingValue = "memory", matchIfMissing = true)
    public TokenBucketStore tokenBucketStore() {
        return new InMemoryTokenBucketStore();
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnProperty(prefix = "ratelimiter", name = "mode", havingValue = "memory", matchIfMissing = true)
    public RateLimiter tokenBucketRateLimiter(TokenBucketStore store) {
        return new TokenBucketRateLimiter(store);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimitingFilter.class)
    public RateLimitingFilter rateLimitingFilter(
        RateLimiter limiter,
        RateLimitPolicyResolver<HttpServletRequest> policyResolver,
        RateLimiterProperties props,
        ObjectProvider<MeterRegistry> meterRegistry
    ) {
        return new RateLimitingFilter(limiter, policyResolver, props, meterRegistry.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean(RateLimitKeyResolver.class)
    public RateLimitKeyResolver<HttpServletRequest> rateLimitKeyResolver(RateLimiterProperties props) {
        return new DefaultHttpRateLimitKeyResolver(props);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimitPolicyResolver.class)
    public RateLimitPolicyResolver<HttpServletRequest> rateLimitPolicyResolver(
        RateLimiterProperties props,
        RateLimitKeyResolver<HttpServletRequest> keyResolver
    ) {
        return new DefaultHttpRateLimitPolicyResolver(props, keyResolver);
    }
}
