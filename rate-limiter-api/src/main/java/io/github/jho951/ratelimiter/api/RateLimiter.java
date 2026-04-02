package io.github.jho951.ratelimiter.api;

/**
 * rate limiter의 핵심 실행 계약.
 *
 * <p>구현체는 key와 plan을 기준으로 허용/차단을 판정하고, 결과를
 * {@link RateLimitDecision}으로 돌려준다.</p>
 */
public interface RateLimiter {
    RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan);
}
