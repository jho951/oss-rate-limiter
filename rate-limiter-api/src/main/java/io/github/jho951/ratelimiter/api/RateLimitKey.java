package io.github.jho951.ratelimiter.api;

import java.util.Objects;

/**
 * rate limiting 대상의 식별자.
 *
 * <p>종류(`RateLimitKeyType`)와 실제 값을 분리해서 다양한 주체를 같은 방식으로 다룬다.</p>
 */
public final class RateLimitKey {

    private final RateLimitKeyType type;
    private final String value;

    private RateLimitKey(RateLimitKeyType type, String value) {
        this.type = Objects.requireNonNull(type, "type");
        this.value = Objects.requireNonNull(value, "value").trim();
        if (this.value.isEmpty()) {
            throw new IllegalArgumentException("value is blank");
        }
    }

    public static RateLimitKey of(RateLimitKeyType type, String value) {
        return new RateLimitKey(type, value);
    }

    public RateLimitKeyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    /**
     * 저장소 키로 쓰기 좋은 문자열 표현.
     */
    public String asString() {
        return type.name() + ":" + value;
    }
}
