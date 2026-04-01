package com.ratelimiter.config;

import com.ratelimiter.api.RateLimitKey;
import com.ratelimiter.api.RateLimitKeyResolver;
import com.ratelimiter.api.RateLimitKeyType;
import com.ratelimiter.api.RateLimitPlan;
import com.ratelimiter.api.RateLimitPolicy;
import com.ratelimiter.api.RateLimitPolicyResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 기본 HTTP 정책 resolver.
 */
class DefaultHttpRateLimitPolicyResolver implements RateLimitPolicyResolver<HttpServletRequest> {

    private final RateLimiterProperties props;
    private final RateLimitKeyResolver<HttpServletRequest> keyResolver;

    DefaultHttpRateLimitPolicyResolver(RateLimiterProperties props, RateLimitKeyResolver<HttpServletRequest> keyResolver) {
        this.props = props;
        this.keyResolver = keyResolver;
    }

    @Override
    public List<RateLimitPolicy> resolve(HttpServletRequest request) {
        List<RateLimitPolicy> policies = new ArrayList<>(3);

        addPolicy(policies, "IP", keyResolver.resolve(request, RateLimitKeyType.IP),
            props.getIp().getCapacity(), props.getIp().getRefillPerSecond());
        addPolicy(policies, "USER_ID", keyResolver.resolve(request, RateLimitKeyType.USER_ID),
            props.getUserId().getCapacity(), props.getUserId().getRefillPerSecond());
        addPolicy(policies, "API_KEY", keyResolver.resolve(request, RateLimitKeyType.API_KEY),
            props.getApiKey().getCapacity(), props.getApiKey().getRefillPerSecond());

        return policies;
    }

    private void addPolicy(List<RateLimitPolicy> policies, String name, RateLimitKey key, long capacity, double refillPerSecond) {
        if (key == null) {
            return;
        }
        policies.add(RateLimitPolicy.of(name, key, RateLimitPlan.perSecond(capacity, refillPerSecond), 1));
    }
}
