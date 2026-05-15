package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 트래픽 제어(Rate Limiting)를 적용할 대상 식별 */
public final class RateLimitKey {
	/** 식별자의 종류/카테고리 */
    private final RateLimitKeyType type;
	/** 식별자의 실제 값 */
    private final String value;

    private RateLimitKey(RateLimitKeyType type, String value) {
        this.type = Objects.requireNonNull(type, "type");
        this.value = Objects.requireNonNull(value, "value").trim();
        if (this.value.isEmpty()) throw new IllegalArgumentException("value is blank");
    }

    public static RateLimitKey of(RateLimitKeyType type, String value) {
        return new RateLimitKey(type, value);
    }

    public RateLimitKeyType getType() {return type;}
    public String getValue() {return value;}

    public String asString() {
        return type.name() + ":" + value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitKey that)) return false;
        return type == that.type && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "RateLimitKey{type=" + type + ", value='" + value + "'}";
    }
}
