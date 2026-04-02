package io.github.jho951.ratelimiter.api;

/**
 * 한 번의 검사에 대한 판정 결과.
 *
 * <p>허용 여부, 남은 토큰 수, 재시도 가능 시점을 최소 계약으로 제공한다.</p>
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
