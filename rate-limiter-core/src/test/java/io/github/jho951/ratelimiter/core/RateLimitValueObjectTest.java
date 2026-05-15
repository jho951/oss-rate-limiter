package io.github.jho951.ratelimiter.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitValueObjectTest {

    @Test
    void keySupportsValueSemantics() {
        RateLimitKey left = RateLimitKey.of(RateLimitKeyType.USER_ID, "user-1");
        RateLimitKey same = RateLimitKey.of(RateLimitKeyType.USER_ID, "user-1");
        RateLimitKey different = RateLimitKey.of(RateLimitKeyType.IP, "user-1");

        assertEquals(left, same);
        assertEquals(left.hashCode(), same.hashCode());
        assertNotEquals(left, different);
        assertEquals("USER_ID:user-1", left.asString());
        assertTrue(left.toString().contains("USER_ID"));
    }

    @Test
    void planSupportsValueSemantics() {
        RateLimitPlan left = RateLimitPlan.perSecond(10, 5.0);
        RateLimitPlan same = RateLimitPlan.perSecond(10, 5.0);
        RateLimitPlan different = RateLimitPlan.perSecond(20, 5.0);

        assertEquals(left, same);
        assertEquals(left.hashCode(), same.hashCode());
        assertNotEquals(left, different);
        assertTrue(left.toString().contains("capacity=10"));
    }

    @Test
    void decisionSupportsValueSemantics() {
        RateLimitDecision allowed = RateLimitDecision.allow(9);
        RateLimitDecision same = RateLimitDecision.allow(9);
        RateLimitDecision denied = RateLimitDecision.deny(0, 1000);

        assertEquals(allowed, same);
        assertEquals(allowed.hashCode(), same.hashCode());
        assertNotEquals(allowed, denied);
        assertTrue(denied.toString().contains("retryAfterMillis=1000"));
    }

    @Test
    void policySupportsValueSemantics() {
        RateLimitPolicy left = RateLimitPolicy.of(
            "per-ip",
            RateLimitKey.of(RateLimitKeyType.IP, "203.0.113.10"),
            RateLimitPlan.perSecond(10, 5.0),
            1
        );
        RateLimitPolicy same = RateLimitPolicy.of(
            "per-ip",
            RateLimitKey.of(RateLimitKeyType.IP, "203.0.113.10"),
            RateLimitPlan.perSecond(10, 5.0),
            1
        );
        RateLimitPolicy different = RateLimitPolicy.of(
            "per-user",
            RateLimitKey.of(RateLimitKeyType.USER_ID, "user-1"),
            RateLimitPlan.perSecond(10, 5.0),
            1
        );

        assertEquals(left, same);
        assertEquals(left.hashCode(), same.hashCode());
        assertNotEquals(left, different);
        assertTrue(left.toString().contains("per-ip"));
    }

    @Test
    void exceptionRequiresDecision() {
        assertThrows(NullPointerException.class, () -> new RateLimitException("blocked", null));
    }

    @Test
    void keyTypeExposesStableEnumValues() {
        assertEquals(RateLimitKeyType.IP, RateLimitKeyType.valueOf("IP"));
        assertEquals(RateLimitKeyType.USER_ID, RateLimitKeyType.valueOf("USER_ID"));
        assertEquals(RateLimitKeyType.API_KEY, RateLimitKeyType.valueOf("API_KEY"));
        assertEquals(RateLimitKeyType.CUSTOM, RateLimitKeyType.valueOf("CUSTOM"));
    }
}
