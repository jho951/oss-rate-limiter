package io.github.jho951.ratelimiter.config;

import io.github.jho951.ratelimiter.api.RateLimitDecision;
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitPlan;
import io.github.jho951.ratelimiter.api.RateLimitPolicyResolver;
import io.github.jho951.ratelimiter.api.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RateLimitingFilterTest {

    @Test
    void skipsExcludedPaths() throws Exception {
        RecordingLimiter limiter = new RecordingLimiter();
        RateLimiterProperties props = new RateLimiterProperties();
        props.setExcludedPaths(List.of("/health"));
        RateLimitPolicyResolver<HttpServletRequest> policyResolver =
            new DefaultHttpRateLimitPolicyResolver(props, new DefaultHttpRateLimitKeyResolver(props));

        RateLimitingFilter filter = new RateLimitingFilter(limiter, policyResolver, props, new SimpleMeterRegistry());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(limiter.calls).isEmpty();
    }

    @Test
    void usesForwardedForOnlyWhenEnabled() throws Exception {
        RecordingLimiter limiter = new RecordingLimiter();
        RateLimiterProperties props = new RateLimiterProperties();
        props.setTrustForwardHeaders(true);
        RateLimitPolicyResolver<HttpServletRequest> policyResolver =
            new DefaultHttpRateLimitPolicyResolver(props, new DefaultHttpRateLimitKeyResolver(props));

        RateLimitingFilter filter = new RateLimitingFilter(limiter, policyResolver, props, new SimpleMeterRegistry());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(limiter.calls).contains("IP:203.0.113.10");
    }

    @Test
    void recordsMetricsForAllowedAndDeniedDecisions() throws Exception {
        RecordingLimiter limiter = new RecordingLimiter();
        limiter.allowNext = false;
        RateLimiterProperties props = new RateLimiterProperties();
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        RateLimitPolicyResolver<HttpServletRequest> policyResolver =
            new DefaultHttpRateLimitPolicyResolver(props, new DefaultHttpRateLimitKeyResolver(props));

        RateLimitingFilter filter = new RateLimitingFilter(limiter, policyResolver, props, registry);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(registry.counter("ratelimiter.decisions", "key_type", "IP", "outcome", "denied").count()).isEqualTo(1.0);
    }

    private static final class RecordingLimiter implements RateLimiter {
        private final List<String> calls = new ArrayList<>();
        private boolean allowNext = true;

        @Override
        public RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan) {
            calls.add(key.asString());
            if (allowNext) {
                return RateLimitDecision.allow(1);
            }
            return RateLimitDecision.deny(0, 1000);
        }
    }
}
