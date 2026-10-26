package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitPolicy;

import java.util.List;

/** 요청이나 컨텍스트에 적용할 정책 목록을 결정하는 계약입니다. */
@FunctionalInterface
public interface RateLimitPolicyResolver<T> {
    /** 원본 요청에 적용할 정책 목록을 계산합니다.
     * @param source 정책을 결정할 요청 또는 컨텍스트
     * @return 적용할 정책 목록
     */
    List<RateLimitPolicy> resolve(T source);
}
