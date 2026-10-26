package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 트래픽 제어를 적용할 대상을 종류와 값으로 식별합니다. */
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

    /** 대상 종류와 값으로 식별 키를 생성합니다.
     * @param type 대상 종류
     * @param value 대상 식별 값. 앞뒤 공백은 제거됩니다.
     * @return 생성된 식별 키
     * @throws NullPointerException 종류 또는 값이 {@code null}인 경우
     * @throws IllegalArgumentException 값이 공백뿐인 경우
     */
    public static RateLimitKey of(RateLimitKeyType type, String value) {
        return new RateLimitKey(type, value);
    }

    /**
     * 대상 종류를 반환합니다.
     * @return 대상 종류
     */
    public RateLimitKeyType getType() {return type;}
    /**
     * 정규화된 대상 식별 값을 반환합니다.
     * @return 대상 값
     */
    public String getValue() {return value;}

    /** 종류와 값을 콜론으로 연결한 문자열을 반환합니다.
     * @return {@code 종류:값} 형식의 문자열
     */
    public String asString() {
        return type.name() + ":" + value;
    }

    @Override
    /**
     * 다른 객체가 이 식별 키와 같은지 비교합니다.
     * @param other 비교할 객체
     * @return 같으면 {@code true}
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitKey)) return false;
        RateLimitKey that = (RateLimitKey) other;
        return type == that.type && value.equals(that.value);
    }

    @Override
    /**
     * 식별 키의 해시 코드를 반환합니다.
     * @return 해시 코드
     */
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    /**
     * 식별 키를 문자열로 반환하며 API 키 값은 노출하지 않습니다.
     * @return 식별 키 문자열
     */
    public String toString() {
        String displayValue = type == RateLimitKeyType.API_KEY ? "[REDACTED]" : value;
        return "RateLimitKey{type=" + type + ", value='" + displayValue + "'}";
    }
}
