package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 거부된 rate limit 판정을 예외 방식으로 전달할 때 사용하는 예외입니다. */
public class RateLimitException extends RuntimeException {

    private final RateLimitDecision decision;

    /** 거부 판정을 담은 예외를 생성합니다.
     * @param message 예외 메시지
     * @param decision 거부 판정 결과
     * @throws NullPointerException 판정 결과가 {@code null}인 경우
     * @throws IllegalArgumentException 판정 결과가 허용인 경우
     */
    public RateLimitException(String message, RateLimitDecision decision) {
        super(message);
        this.decision = Objects.requireNonNull(decision, "decision");
        if (decision.isAllowed()) {
            throw new IllegalArgumentException("decision must represent a denied request");
        }
    }

    /**
     * 이 예외에 담긴 거부 판정을 반환합니다.
     * @return 거부 판정 결과
     */
    public RateLimitDecision getDecision() {
        return decision;
    }
}
