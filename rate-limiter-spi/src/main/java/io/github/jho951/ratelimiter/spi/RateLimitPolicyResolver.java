package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitPolicy;

import java.util.List;

/** 요청이나 컨텍스트에 적용할 정책 목록을 만드는 계약 */
@FunctionalInterface
public interface RateLimitPolicyResolver<T> {
    List<RateLimitPolicy> resolve(T source);
}
