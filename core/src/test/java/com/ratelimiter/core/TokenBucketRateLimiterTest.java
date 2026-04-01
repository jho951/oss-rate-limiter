package com.ratelimiter.core;

import com.ratelimiter.api.RateLimitDecision;
import com.ratelimiter.api.RateLimitKey;
import com.ratelimiter.api.RateLimitKeyType;
import com.ratelimiter.api.RateLimitPlan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketRateLimiterTest {

    @Test
    void allowsThenDeniesThenRefills() {
        MutableClock clock = new MutableClock(0L);
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(clock, new InMemoryTokenBucketStore());
        RateLimitPlan plan = new RateLimitPlan(2, 1);
        RateLimitKey key = RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4");

        RateLimitDecision first = limiter.tryAcquire(key, 1, plan);
        RateLimitDecision second = limiter.tryAcquire(key, 1, plan);
        RateLimitDecision third = limiter.tryAcquire(key, 1, plan);

        assertTrue(first.isAllowed());
        assertTrue(second.isAllowed());
        assertFalse(third.isAllowed());

        clock.advanceNanos(1_000_000_000L);
        RateLimitDecision fourth = limiter.tryAcquire(key, 1, plan);
        assertTrue(fourth.isAllowed());
    }

    private static final class MutableClock implements Clock {
        private long now;

        private MutableClock(long start) {
            this.now = start;
        }

        @Override
        public long nanoTime() {
            return now;
        }

        private void advanceNanos(long delta) {
            now += delta;
        }
    }
}
