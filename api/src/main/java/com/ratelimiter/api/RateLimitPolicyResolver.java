package com.ratelimiter.api;

import java.util.List;

/**
 * 요청/컨텍스트에 적용할 정책 목록을 만든다.
 */
@FunctionalInterface
public interface RateLimitPolicyResolver<T> {
    List<RateLimitPolicy> resolve(T source);
}
