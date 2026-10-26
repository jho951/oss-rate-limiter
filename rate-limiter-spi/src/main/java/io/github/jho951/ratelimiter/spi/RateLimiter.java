package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitDecision;
import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitPlan;

/** 지정한 대상에 토큰 사용을 요청하는 rate limiter 실행 계약입니다. */
@FunctionalInterface
public interface RateLimiter {
    /** 지정한 수량의 토큰을 획득할 수 있는지 원자적으로 판정합니다.
     * @param key 토큰 버킷을 구분할 대상 식별 키
     * @param permits 획득할 토큰 수
     * @param plan 토큰 버킷 계획
     * @return 허용 또는 거부 판정 결과
     */
    RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan);
}
