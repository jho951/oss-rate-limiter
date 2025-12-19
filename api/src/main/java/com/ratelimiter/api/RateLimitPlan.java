package com.ratelimiter.api;

/**
 * 버킷 정책(용량 + 리필 속도).
 */
public final class RateLimitPlan {

    private final long capacity;
    private final double refillTokensPerSecond;

    public RateLimitPlan(long capacity, double refillTokensPerSecond) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        if (refillTokensPerSecond <= 0) throw new IllegalArgumentException("refillTokensPerSecond must be > 0");
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    public long getCapacity() {
        return capacity;
    }

    public double getRefillTokensPerSecond() {
        return refillTokensPerSecond;
    }

    public static RateLimitPlan perSecond(long capacity, double refillPerSecond) {
        return new RateLimitPlan(capacity, refillPerSecond);
    }
}
