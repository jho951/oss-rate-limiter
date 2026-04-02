package io.github.jho951.ratelimiter.api;

import java.util.List;

/**
 * 요청이나 컨텍스트에 적용할 정책 목록을 만드는 계약.
 *
 * <p>각 정책은 key, plan, permits를 묶어 하나의 rate limit 검사 단위를 표현한다.</p>
 */
@FunctionalInterface
public interface RateLimitPolicyResolver<T> {
    List<RateLimitPolicy> resolve(T source);
}
