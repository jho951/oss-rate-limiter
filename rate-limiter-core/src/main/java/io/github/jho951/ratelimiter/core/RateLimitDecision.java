package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 한 번의 rate limit 검사 결과 */
public final class RateLimitDecision {
	/** 패스 여부 */
    private final boolean allowed;
	/** 남은 잔여량 */
    private final long remainingTokens;
	/** 재시도 대기 시간 */
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

    public boolean isAllowed() {return allowed;}
    public long getRemainingTokens() {return remainingTokens;}
    public long getRetryAfterMillis() {return retryAfterMillis;}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitDecision that)) return false;
        return allowed == that.allowed
            && remainingTokens == that.remainingTokens
            && retryAfterMillis == that.retryAfterMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowed, remainingTokens, retryAfterMillis);
    }

    @Override
    public String toString() {
        return "RateLimitDecision{allowed=" + allowed
            + ", remainingTokens=" + remainingTokens
            + ", retryAfterMillis=" + retryAfterMillis + "}";
    }
}
