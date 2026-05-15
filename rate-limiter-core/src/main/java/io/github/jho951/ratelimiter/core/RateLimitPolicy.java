package io.github.jho951.ratelimiter.core;

import java.util.Objects;

/** 실제 트래픽 제어를 수행할 최종 결합형 정책 */
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
        this.name = Objects.requireNonNull(name, "name");
        this.key = Objects.requireNonNull(key, "key");
        this.plan = Objects.requireNonNull(plan, "plan");
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        this.permits = permits;
    }

    public static RateLimitPolicy of(String name, RateLimitKey key, RateLimitPlan plan, long permits) {
        return new RateLimitPolicy(name, key, plan, permits);
    }

    public String getName() {return name;}
    public RateLimitKey getKey() {return key;}
    public RateLimitPlan getPlan() {return plan;}
    public long getPermits() {return permits;}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RateLimitPolicy that)) return false;
        return permits == that.permits
            && name.equals(that.name)
            && key.equals(that.key)
            && plan.equals(that.plan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, plan, permits);
    }

    @Override
    public String toString() {
        return "RateLimitPolicy{name='" + name + "', key=" + key
            + ", plan=" + plan + ", permits=" + permits + "}";
    }
}
