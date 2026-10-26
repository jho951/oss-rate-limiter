package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 토큰 버킷의 최대 용량과 초당 충전량을 정의합니다. */
public final class RateLimitPlan {
	/** 버킷 최대 용량 */
    private final long capacity;
	/** 토큰 초당 충전량 */
    private final double refillTokensPerSecond;

    /** 토큰 버킷 계획을 생성합니다.
     * @param capacity 버킷의 최대 토큰 수
     * @param refillTokensPerSecond 초당 충전할 토큰 수
     * @throws IllegalArgumentException 용량 또는 충전량이 유효하지 않은 경우
     */
    public RateLimitPlan(long capacity, double refillTokensPerSecond) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        if (Double.isNaN(refillTokensPerSecond)
            || Double.isInfinite(refillTokensPerSecond)
            || refillTokensPerSecond <= 0) {
            throw new IllegalArgumentException("refillTokensPerSecond must be a finite value > 0");
        }
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    /**
     * 최대 토큰 용량을 반환합니다.
     * @return 최대 토큰 수
     */
    public long getCapacity() {return capacity;}
    /**
     * 초당 토큰 충전량을 반환합니다.
     * @return 초당 충전 토큰 수
     */
    public double getRefillTokensPerSecond() {return refillTokensPerSecond;}

    /** 초 단위 충전량을 사용하는 토큰 버킷 계획을 생성합니다.
     * @param capacity 버킷의 최대 토큰 수
     * @param refillPerSecond 초당 충전할 토큰 수
     * @return 생성된 토큰 버킷 계획
     */
    public static RateLimitPlan perSecond(long capacity, double refillPerSecond) {
        return new RateLimitPlan(capacity, refillPerSecond);
    }

    @Override
    /**
     * 다른 객체가 이 계획과 같은지 비교합니다.
     * @param other 비교할 객체
     * @return 같으면 {@code true}
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitPlan)) return false;
        RateLimitPlan that = (RateLimitPlan) other;
        return capacity == that.capacity
            && Double.compare(refillTokensPerSecond, that.refillTokensPerSecond) == 0;
    }

    @Override
    /**
     * 계획의 해시 코드를 반환합니다.
     * @return 해시 코드
     */
    public int hashCode() {
        return Objects.hash(capacity, refillTokensPerSecond);
    }

    @Override
    /**
     * 계획을 문자열로 반환합니다.
     * @return 계획 문자열
     */
    public String toString() {
        return "RateLimitPlan{capacity=" + capacity
            + ", refillTokensPerSecond=" + refillTokensPerSecond + "}";
    }
}
