package com.ratelimiter.core;

import com.ratelimiter.api.RateLimitDecision;
import com.ratelimiter.api.RateLimitKey;
import com.ratelimiter.api.RateLimitPlan;
import com.ratelimiter.api.RateLimiter;

/**
 * Token Bucket 기반 Rate Limiter (v1).
 * - key별로 token bucket을 하나씩 운영
 * - synchronized(state)로 key 단위 동기화
 */
public final class TokenBucketRateLimiter implements RateLimiter {

    private final Clock clock;
    private final InMemoryTokenBucketStore store;

    public TokenBucketRateLimiter() {
        this(Clock.system(), new InMemoryTokenBucketStore());
    }

    public TokenBucketRateLimiter(Clock clock, InMemoryTokenBucketStore store) {
        this.clock = clock;
        this.store = store;
    }

    @Override
    public RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan) {
        if (permits <= 0) permits = 1;

        long now = clock.nanoTime();
        String storeKey = key.asString();

        TokenBucketState state = store.getOrCreate(storeKey, plan.getCapacity(), now);

        synchronized (state) { // key별 버킷 단위 락
            refill(state, plan, now);
            state.lastAccessNanos = now;

            if (state.tokens >= permits) {
                state.tokens -= permits;
                return RateLimitDecision.allow((long) Math.floor(state.tokens));
            }

            double need = permits - state.tokens;
            double seconds = need / plan.getRefillTokensPerSecond();
            long retryAfterMillis = (long) Math.ceil(seconds * 1000.0);

            return RateLimitDecision.deny((long) Math.floor(state.tokens), retryAfterMillis);
        }
    }

    private void refill(TokenBucketState state, RateLimitPlan plan, long now) {
        long elapsedNanos = now - state.lastRefillNanos;
        if (elapsedNanos <= 0) return;

        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
        double refill = elapsedSeconds * plan.getRefillTokensPerSecond();

        state.tokens = Math.min(plan.getCapacity(), state.tokens + refill);
        state.lastRefillNanos = now;
    }
}
