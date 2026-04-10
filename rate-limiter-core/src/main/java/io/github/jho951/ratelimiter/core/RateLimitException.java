package io.github.jho951.ratelimiter.core;

/**
 * 예외 기반 차단 처리가 필요한 경우 사용하는 선택적 예외.
 */
public class RateLimitException extends RuntimeException {

    private final RateLimitDecision decision;

    public RateLimitException(String message, RateLimitDecision decision) {
        super(message);
        this.decision = decision;
    }

    public RateLimitDecision getDecision() {
        return decision;
    }
}
