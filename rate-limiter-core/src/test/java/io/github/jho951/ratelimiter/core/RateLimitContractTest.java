package io.github.jho951.ratelimiter.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitContractTest {

    @Test
    void keyRejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> RateLimitKey.of(RateLimitKeyType.IP, " "));
    }

    @Test
    void keyRejectsNullTypeAndValue() {
        assertThrows(NullPointerException.class, () -> RateLimitKey.of(null, "1.2.3.4"));
        assertThrows(NullPointerException.class, () -> RateLimitKey.of(RateLimitKeyType.IP, null));
    }

    @Test
    void keyTrimsValueBeforeStoring() {
        RateLimitKey key = RateLimitKey.of(RateLimitKeyType.IP, " 203.0.113.10 ");

        assertEquals("203.0.113.10", key.getValue());
        assertEquals("IP:203.0.113.10", key.asString());
    }

    @Test
    void planRejectsNonPositiveValues() {
        assertThrows(IllegalArgumentException.class, () -> new RateLimitPlan(0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitPlan(1, 0.0));
    }

    @Test
    void policyRejectsNullRequiredFields() {
        RateLimitKey key = RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4");
        RateLimitPlan plan = RateLimitPlan.perSecond(10, 1.0);

        assertThrows(NullPointerException.class, () -> RateLimitPolicy.of(null, key, plan, 1));
        assertThrows(NullPointerException.class, () -> RateLimitPolicy.of("ip-limit", null, plan, 1));
        assertThrows(NullPointerException.class, () -> RateLimitPolicy.of("ip-limit", key, null, 1));
    }

    @Test
    void decisionClampsNegativeNumbers() {
        RateLimitDecision allowed = RateLimitDecision.allow(-10);
        RateLimitDecision denied = RateLimitDecision.deny(-5, -7);

        assertTrue(allowed.isAllowed());
        assertEquals(0, allowed.getRemainingTokens());
        assertEquals(0, allowed.getRetryAfterMillis());
        assertFalse(denied.isAllowed());
        assertEquals(0, denied.getRemainingTokens());
        assertEquals(0, denied.getRetryAfterMillis());
    }

    @Test
    void policyRejectsNonPositivePermits() {
        assertThrows(IllegalArgumentException.class, () -> RateLimitPolicy.of(
            "ip-limit",
            RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
            RateLimitPlan.perSecond(10, 1.0),
            0
        ));
    }

    @Test
    void exceptionKeepsMessageAndDecision() {
        RateLimitDecision decision = RateLimitDecision.deny(0, 1000);
        RateLimitException exception = new RateLimitException("blocked", decision);

        assertEquals("blocked", exception.getMessage());
        assertSame(decision, exception.getDecision());
        assertNull(exception.getCause());
    }
}
