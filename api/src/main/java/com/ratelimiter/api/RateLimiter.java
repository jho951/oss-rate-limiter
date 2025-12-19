package com.ratelimiter.api;

/**
 * Rate limiter 핵심 인터페이스.
 */
public interface RateLimiter {
    RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan);
}
