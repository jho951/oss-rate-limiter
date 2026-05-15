package io.github.jho951.ratelimiter.spi;

import io.github.jho951.ratelimiter.core.RateLimitDecision;
import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitKeyType;
import io.github.jho951.ratelimiter.core.RateLimitPlan;
import io.github.jho951.ratelimiter.core.RateLimitPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterSpiContractTest {

    @Test
    void resolverAndPolicyContractsComposeIntoAMinimalFlow() {
        RequestContext request = new RequestContext("203.0.113.10");

        RateLimitKeyResolver<RequestContext> keyResolver =
            (source, type) -> RateLimitKey.of(type, source.clientIp());

        RateLimitPolicyResolver<RequestContext> policyResolver =
            source -> List.of(
                RateLimitPolicy.of(
                    "per-ip",
                    keyResolver.resolve(source, RateLimitKeyType.IP),
                    RateLimitPlan.perSecond(10, 5.0),
                    1
                )
            );

        RateLimitPolicy policy = policyResolver.resolve(request).get(0);

        assertEquals("per-ip", policy.getName());
        assertEquals(RateLimitKeyType.IP, policy.getKey().getType());
        assertEquals("203.0.113.10", policy.getKey().getValue());
        assertEquals(1, policy.getPermits());
        assertEquals(RateLimitPlan.perSecond(10, 5.0), policy.getPlan());
    }

    @Test
    void keyResolverCanSelectDifferentKeyTypes() {
        RequestContext request = new RequestContext("203.0.113.10");
        RateLimitKeyResolver<RequestContext> keyResolver =
            (source, type) -> RateLimitKey.of(type, source.clientIp());

        RateLimitKey ipKey = keyResolver.resolve(request, RateLimitKeyType.IP);
        RateLimitKey customKey = keyResolver.resolve(request, RateLimitKeyType.CUSTOM);

        assertEquals(RateLimitKeyType.IP, ipKey.getType());
        assertEquals(RateLimitKeyType.CUSTOM, customKey.getType());
        assertEquals("203.0.113.10", customKey.getValue());
    }

    @Test
    void rateLimiterCanBeImplementedWithALambda() {
        RateLimiter limiter = (key, permits, plan) ->
            permits <= plan.getCapacity()
                ? RateLimitDecision.allow(plan.getCapacity() - permits)
                : RateLimitDecision.deny(0, 1000);

        RateLimitDecision allowed = limiter.tryAcquire(
            RateLimitKey.of(RateLimitKeyType.IP, "203.0.113.10"),
            1,
            RateLimitPlan.perSecond(10, 5.0)
        );
        RateLimitDecision denied = limiter.tryAcquire(
            RateLimitKey.of(RateLimitKeyType.IP, "203.0.113.10"),
            11,
            RateLimitPlan.perSecond(10, 5.0)
        );

        assertTrue(allowed.isAllowed());
        assertEquals(9, allowed.getRemainingTokens());
        assertFalse(denied.isAllowed());
        assertEquals(1000, denied.getRetryAfterMillis());
    }

    @Test
    void policyResolverCanReturnMultiplePoliciesForOneSource() {
        RequestContext request = new RequestContext("203.0.113.10");
        RateLimitPolicy ipPolicy = RateLimitPolicy.of(
            "per-ip",
            RateLimitKey.of(RateLimitKeyType.IP, request.clientIp()),
            RateLimitPlan.perSecond(10, 5.0),
            1
        );
        RateLimitPolicy customPolicy = RateLimitPolicy.of(
            "per-client",
            RateLimitKey.of(RateLimitKeyType.CUSTOM, "client:" + request.clientIp()),
            RateLimitPlan.perSecond(20, 10.0),
            2
        );
        RateLimitPolicyResolver<RequestContext> policyResolver =
            source -> List.of(ipPolicy, customPolicy);

        List<RateLimitPolicy> policies = policyResolver.resolve(request);

        assertEquals(2, policies.size());
        assertSame(ipPolicy, policies.get(0));
        assertSame(customPolicy, policies.get(1));
    }

    private record RequestContext(String clientIp) {
    }
}
