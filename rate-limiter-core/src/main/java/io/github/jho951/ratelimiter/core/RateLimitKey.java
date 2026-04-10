package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/**
 * rate limiting 대상의 식별자.
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

    public String asString() {
        return type.name() + ":" + value;
    }
}
