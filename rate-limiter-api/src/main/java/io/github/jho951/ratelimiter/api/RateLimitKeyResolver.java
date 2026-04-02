package io.github.jho951.ratelimiter.api;

/**
 * 요청이나 컨텍스트에서 `RateLimitKey`를 추출하는 계약.
 *
 * <p>기본 구현은 HTTP 요청에 맞춰 제공되지만, 이 인터페이스 자체는
 * Spring, Servlet, Redis와 무관한 순수 계약이다.</p>
 */
@FunctionalInterface
public interface RateLimitKeyResolver<T> {
    RateLimitKey resolve(T source, RateLimitKeyType type);
}
