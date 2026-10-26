package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 이름, 대상, 버킷 계획, 요청 토큰 수를 결합한 rate limit 정책입니다. */
public final class RateLimitPolicy {

	/** 정책 이름 */
    private final String name;
	/** 정책 대상 */
    private final RateLimitKey key;
	/** 규칙 */
    private final RateLimitPlan plan;
	/** 소모 토큰 수 */
    private final long permits;

    private RateLimitPolicy(String name, RateLimitKey key, RateLimitPlan plan, long permits) {
        this.name = Objects.requireNonNull(name, "name").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("name is blank");
        this.key = Objects.requireNonNull(key, "key");
        this.plan = Objects.requireNonNull(plan, "plan");
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        if (permits > plan.getCapacity()) {
            throw new IllegalArgumentException("permits must not exceed plan capacity");
        }
        this.permits = permits;
    }

    /** 각 정책 구성 요소를 결합해 정책을 생성합니다.
     * @param name 공백을 제거한 정책 이름
     * @param key 제한 대상 식별 키
     * @param plan 토큰 버킷 계획
     * @param permits 요청 한 건에 필요한 토큰 수
     * @return 생성된 정책
     * @throws NullPointerException 필수 구성 요소가 {@code null}인 경우
     * @throws IllegalArgumentException 이름이 비었거나 토큰 수가 유효하지 않은 경우
     */
    public static RateLimitPolicy of(String name, RateLimitKey key, RateLimitPlan plan, long permits) {
        return new RateLimitPolicy(name, key, plan, permits);
    }

    /**
     * 정책 이름을 반환합니다.
     * @return 정책 이름
     */
    public String getName() {return name;}
    /**
     * 제한 대상을 반환합니다.
     * @return 대상 식별 키
     */
    public RateLimitKey getKey() {return key;}
    /**
     * 토큰 버킷 계획을 반환합니다.
     * @return 버킷 계획
     */
    public RateLimitPlan getPlan() {return plan;}
    /**
     * 요청 한 건에 필요한 토큰 수를 반환합니다.
     * @return 요청 토큰 수
     */
    public long getPermits() {return permits;}

    @Override
    /**
     * 다른 객체가 이 정책과 같은지 비교합니다.
     * @param other 비교할 객체
     * @return 같으면 {@code true}
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitPolicy)) return false;
        RateLimitPolicy that = (RateLimitPolicy) other;
        return permits == that.permits
            && name.equals(that.name)
            && key.equals(that.key)
            && plan.equals(that.plan);
    }

    @Override
    /**
     * 정책의 해시 코드를 반환합니다.
     * @return 해시 코드
     */
    public int hashCode() {
        return Objects.hash(name, key, plan, permits);
    }

    @Override
    /**
     * 정책을 문자열로 반환합니다.
     * @return 정책 문자열
     */
    public String toString() {
        return "RateLimitPolicy{name='" + name + "', key=" + key
            + ", plan=" + plan + ", permits=" + permits + "}";
    }
}
