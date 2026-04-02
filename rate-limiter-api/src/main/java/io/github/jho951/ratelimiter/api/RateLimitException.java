package io.github.jho951.ratelimiter.api;

/**
 * 예외 기반 차단 처리가 필요할 때 사용하는 선택적 예외.
 *
 * <p>기본 API는 `RateLimitDecision` 반환형을 사용하고, 이 예외는
 * 어댑터 레이어에서 실패 응답으로 변환할 때만 쓰는 보조 타입이다.</p>
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
