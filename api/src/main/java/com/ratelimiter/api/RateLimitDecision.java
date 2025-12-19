package com.ratelimiter.api;

/**
 * 한 번의 요청에 대한 Rate Limit 판단 결과.
 */
public final class RateLimitDecision {

    private final boolean allowed;
    private final long remainingTokens;
    private final long retryAfterMillis;

    private RateLimitDecision(boolean allowed, long remainingTokens, long retryAfterMillis) {
        this.allowed = allowed;
        this.remainingTokens = Math.max(0, remainingTokens);
        this.retryAfterMillis = Math.max(0, retryAfterMillis);
    }

    public static RateLimitDecision allow(long remainingTokens) {
        return new RateLimitDecision(true, remainingTokens, 0);
    }

    public static RateLimitDecision deny(long remainingTokens, long retryAfterMillis) {
        return new RateLimitDecision(false, remainingTokens, retryAfterMillis);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }

    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }
}
