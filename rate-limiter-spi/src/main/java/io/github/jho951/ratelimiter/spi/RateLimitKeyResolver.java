package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitKeyType;

/**
 * 요청이나 컨텍스트에서 `RateLimitKey`를 추출하는 계약.
 */
@FunctionalInterface
public interface RateLimitKeyResolver<T> {
    RateLimitKey resolve(T source, RateLimitKeyType type);
}
