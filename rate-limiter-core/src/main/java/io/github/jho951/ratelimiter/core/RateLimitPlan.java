package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 토큰 버킷(Token Bucket) 알고리즘을 구동 정책 */
public final class RateLimitPlan {
	/** 버킷 최대 용량 */
    private final long capacity;
	/** 토큰 초당 충전량 */
    private final double refillTokensPerSecond;

    public RateLimitPlan(long capacity, double refillTokensPerSecond) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        if (refillTokensPerSecond <= 0) throw new IllegalArgumentException("refillTokensPerSecond must be > 0");
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    public long getCapacity() {return capacity;}
    public double getRefillTokensPerSecond() {return refillTokensPerSecond;}

    public static RateLimitPlan perSecond(long capacity, double refillPerSecond) {
        return new RateLimitPlan(capacity, refillPerSecond);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitPlan that)) return false;
        return capacity == that.capacity
            && Double.compare(refillTokensPerSecond, that.refillTokensPerSecond) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, refillTokensPerSecond);
    }

    @Override
    public String toString() {
        return "RateLimitPlan{capacity=" + capacity
            + ", refillTokensPerSecond=" + refillTokensPerSecond + "}";
    }
}
