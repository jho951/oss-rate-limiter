package io.github.jho951.ratelimiter.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitContractTest {

    @Test
    void keyRejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> RateLimitKey.of(RateLimitKeyType.IP, " "));
    }

    @Test
    void planRejectsNonPositiveValues() {
        assertThrows(IllegalArgumentException.class, () -> new RateLimitPlan(0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitPlan(1, 0.0));
    }

    @Test
    void decisionClampsNegativeNumbers() {
        RateLimitDecision allowed = RateLimitDecision.allow(-10);
        RateLimitDecision denied = RateLimitDecision.deny(-5, -7);

        assertTrue(allowed.isAllowed());
        assertEquals(0, allowed.getRemainingTokens());
        assertFalse(denied.isAllowed());
        assertEquals(0, denied.getRemainingTokens());
        assertEquals(0, denied.getRetryAfterMillis());
    }

    @Test
    void policyNormalizesPermits() {
        RateLimitPolicy policy = RateLimitPolicy.of(
            "ip-limit",
            RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
            RateLimitPlan.perSecond(10, 1.0),
            0
        );

        assertEquals(1, policy.getPermits());
    }
}
