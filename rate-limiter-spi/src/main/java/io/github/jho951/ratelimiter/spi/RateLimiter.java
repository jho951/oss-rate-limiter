package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitDecision;
import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitPlan;

/**
 * rate limiter의 핵심 실행 계약.
 */
public interface RateLimiter {
    RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan);
}
