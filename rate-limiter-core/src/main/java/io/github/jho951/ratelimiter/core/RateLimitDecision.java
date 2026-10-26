package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 한 번의 요청에 대한 rate limit 판정 결과입니다. */
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

    /** 요청을 허용하는 판정 결과를 생성합니다.
     * @param remainingTokens 판정 후 남은 토큰 수
     * @return 허용 판정 결과
     */
    public static RateLimitDecision allow(long remainingTokens) {
        return new RateLimitDecision(true, remainingTokens, 0);
    }

    /** 요청을 거부하는 판정 결과를 생성합니다.
     * @param remainingTokens 판정 후 남은 토큰 수
     * @param retryAfterMillis 다시 시도하기까지 기다릴 시간(밀리초)
     * @return 거부 판정 결과
     */
    public static RateLimitDecision deny(long remainingTokens, long retryAfterMillis) {
        return new RateLimitDecision(false, remainingTokens, retryAfterMillis);
    }

    /**
     * 요청 허용 여부를 반환합니다.
     * @return 허용되면 {@code true}
     */
    public boolean isAllowed() {return allowed;}
    /**
     * 남은 토큰 수를 반환합니다.
     * @return 0 이상의 남은 토큰 수
     */
    public long getRemainingTokens() {return remainingTokens;}
    /**
     * 재시도 대기 시간을 반환합니다.
     * @return 밀리초 단위 대기 시간
     */
    public long getRetryAfterMillis() {return retryAfterMillis;}

    @Override
    /**
     * 다른 객체가 이 판정 결과와 같은지 비교합니다.
     * @param other 비교할 객체
     * @return 같으면 {@code true}
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitDecision)) return false;
        RateLimitDecision that = (RateLimitDecision) other;
        return allowed == that.allowed
            && remainingTokens == that.remainingTokens
            && retryAfterMillis == that.retryAfterMillis;
    }

    @Override
    /**
     * 판정 결과의 해시 코드를 반환합니다.
     * @return 해시 코드
     */
    public int hashCode() {
        return Objects.hash(allowed, remainingTokens, retryAfterMillis);
    }

    @Override
    /**
     * 판정 결과를 문자열로 반환합니다.
     * @return 판정 결과 문자열
     */
    public String toString() {
        return "RateLimitDecision{allowed=" + allowed
            + ", remainingTokens=" + remainingTokens
            + ", retryAfterMillis=" + retryAfterMillis + "}";
    }
}
