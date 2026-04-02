package io.github.jho951.ratelimiter.config;

import io.github.jho951.ratelimiter.api.RateLimitDecision;
import io.github.jho951.ratelimiter.api.RateLimiter;
import io.github.jho951.ratelimiter.api.RateLimitPolicy;
import io.github.jho951.ratelimiter.api.RateLimitPolicyResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * v1: 요청마다 IP / USER_ID / API_KEY 각각 1토큰 차감.
 * - 429 응답 + Retry-After(초) 헤더 반환
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter limiter;
    private final RateLimitPolicyResolver<HttpServletRequest> policyResolver;
    private final RateLimiterProperties props;
    private final MeterRegistry meterRegistry;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RateLimitingFilter(
        RateLimiter limiter,
        RateLimitPolicyResolver<HttpServletRequest> policyResolver,
        RateLimiterProperties props,
        MeterRegistry meterRegistry
    ) {
        this.limiter = limiter;
        this.policyResolver = policyResolver;
        this.props = props;
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        if (isExcludedPath(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        try {
            List<RateLimitPolicy> policies = policyResolver.resolve(request);
            for (RateLimitPolicy policy : policies) {
                RateLimitDecision decision = limiter.tryAcquire(policy.getKey(), policy.getPermits(), policy.getPlan());
                recordDecision(policy, decision);
                if (!decision.isAllowed()) {
                    reject(response, policy.getName() + " rate limited", decision);
                    return;
                }
            }
        } catch (RuntimeException ex) {
            recordError();
            if (props.isFailOpen()) {
                chain.doFilter(request, response);
                return;
            }
            throw new ServletException("rate limiter failed", ex);
        }

        chain.doFilter(request, response);
    }

    private boolean isExcludedPath(String requestUri) {
        List<String> excludedPaths = props.getExcludedPaths();
        if (excludedPaths == null || excludedPaths.isEmpty()) {
            return false;
        }

        for (String pattern : excludedPaths) {
            if (pattern != null && !pattern.isBlank() && pathMatcher.match(pattern.trim(), requestUri)) {
                return true;
            }
        }
        return false;
    }

    private void recordDecision(RateLimitPolicy policy, RateLimitDecision decision) {
        if (meterRegistry == null) {
            return;
        }

        Counter.builder("ratelimiter.decisions")
            .tag("key_type", policy.getKey().getType().name())
            .tag("outcome", decision.isAllowed() ? "allowed" : "denied")
            .register(meterRegistry)
            .increment();
    }

    private void recordError() {
        if (meterRegistry == null) {
            return;
        }

        Counter.builder("ratelimiter.decisions")
            .tag("key_type", "error")
            .tag("outcome", "error")
            .register(meterRegistry)
            .increment();
    }

    private void reject(HttpServletResponse response, String detail, RateLimitDecision d) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json; charset=utf-8");

        // Retry-After는 seconds 단위(관례)
        long retryAfterSeconds = Math.max(1, d.getRetryAfterMillis() / 1000);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
		String body = """
    {
        "message": "Too Many Requests",
        "detail": "%s",
        "retryAfterMillis": %d,
        "remaining": %d
    }
    """.formatted(escape(detail), d.getRetryAfterMillis(), d.getRemainingTokens());
        response.getWriter().write(body);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\");
    }
}
