package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitKeyType;

/** 요청이나 컨텍스트에서 rate limit 식별 키를 추출하는 계약입니다. */
@FunctionalInterface
public interface RateLimitKeyResolver<T> {
    /** 원본 요청에서 지정한 종류의 식별 키를 계산합니다.
     * @param source 키를 추출할 요청 또는 컨텍스트
     * @param type 추출할 식별자 종류
     * @return 추출한 식별 키
     */
    RateLimitKey resolve(T source, RateLimitKeyType type);
}
