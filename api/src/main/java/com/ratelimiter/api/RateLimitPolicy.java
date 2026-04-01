package com.ratelimiter.api;

import java.util.Objects;

/**
 * 하나의 rate limit 검사 단위.
 */
public final class RateLimitPolicy {

    private final String name;
    private final RateLimitKey key;
    private final RateLimitPlan plan;
    private final long permits;

    private RateLimitPolicy(String name, RateLimitKey key, RateLimitPlan plan, long permits) {
        this.name = Objects.requireNonNull(name, "name");
        this.key = Objects.requireNonNull(key, "key");
        this.plan = Objects.requireNonNull(plan, "plan");
        this.permits = permits <= 0 ? 1 : permits;
    }

    public static RateLimitPolicy of(String name, RateLimitKey key, RateLimitPlan plan, long permits) {
        return new RateLimitPolicy(name, key, plan, permits);
    }

    public String getName() {
        return name;
    }

    public RateLimitKey getKey() {
        return key;
    }

    public RateLimitPlan getPlan() {
        return plan;
    }

    public long getPermits() {
        return permits;
    }
}
