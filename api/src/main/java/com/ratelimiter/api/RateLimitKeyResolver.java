package com.ratelimiter.api;

/**
 * 요청/컨텍스트에서 rate limit key를 추출한다.
 */
@FunctionalInterface
public interface RateLimitKeyResolver<T> {
    RateLimitKey resolve(T source, RateLimitKeyType type);
}
