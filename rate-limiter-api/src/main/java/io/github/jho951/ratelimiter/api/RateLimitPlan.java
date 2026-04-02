package io.github.jho951.ratelimiter.api;

/**
 * token bucket의 정책.
 *
 * <p>한 버킷이 가질 수 있는 최대 토큰 수(capacity)와 초당 보충 속도(refill rate)를 담는다.</p>
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
