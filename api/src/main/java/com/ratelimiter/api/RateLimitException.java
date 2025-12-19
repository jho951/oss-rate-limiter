package com.ratelimiter.api;

/**
 * 강제 예외형 사용이 필요할 때(선택).
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
