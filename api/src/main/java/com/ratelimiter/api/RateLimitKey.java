package com.ratelimiter.api;

import java.util.Objects;

/**
 * Rate limiting 키(종류 + 값).
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
     * 저장소 키로 쓰기 좋은 문자열 형태.
     */
    public String asString() {
        return type.name() + ":" + value;
    }
}
