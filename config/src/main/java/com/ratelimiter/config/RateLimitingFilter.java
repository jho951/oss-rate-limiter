package com.ratelimiter.config;

import com.ratelimiter.api.RateLimitDecision;
import com.ratelimiter.api.RateLimitKey;
import com.ratelimiter.api.RateLimitKeyType;
import com.ratelimiter.api.RateLimitPlan;
import com.ratelimiter.core.TokenBucketRateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * v1: 요청마다 IP / USER_ID / API_KEY 각각 1토큰 차감.
 * - 429 응답 + Retry-After(초) 헤더 반환
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private final TokenBucketRateLimiter limiter;
    private final RateLimiterProperties props;

    public RateLimitingFilter(TokenBucketRateLimiter limiter, RateLimiterProperties props) {
        this.limiter = limiter;
        this.props = props;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        // 1) IP
        String ip = resolveClientIp(request);
        if (ip != null) {
            RateLimitDecision d = limiter.tryAcquire(
                RateLimitKey.of(RateLimitKeyType.IP, ip),
                1,
                new RateLimitPlan(props.getIp().getCapacity(), props.getIp().getRefillPerSecond())
            );
            if (!d.isAllowed()) { reject(response, "IP rate limited", d); return; }
        }

        // 2) USER_ID (Principal 우선, 없으면 헤더)
        String userId = resolveUserId(request);
        if (userId != null) {
            RateLimitDecision d = limiter.tryAcquire(
                RateLimitKey.of(RateLimitKeyType.USER_ID, userId),
                1,
                new RateLimitPlan(props.getUserId().getCapacity(), props.getUserId().getRefillPerSecond())
            );
            if (!d.isAllowed()) { reject(response, "USER_ID rate limited", d); return; }
        }

        // 3) API_KEY
        String apiKey = request.getHeader(props.getHeader().getApiKeyHeader());
        if (apiKey != null && !apiKey.isBlank()) {
            RateLimitDecision d = limiter.tryAcquire(
                RateLimitKey.of(RateLimitKeyType.API_KEY, apiKey.trim()),
                1,
                new RateLimitPlan(props.getApiKey().getCapacity(), props.getApiKey().getRefillPerSecond())
            );
            if (!d.isAllowed()) { reject(response, "API_KEY rate limited", d); return; }
        }

        chain.doFilter(request, response);
    }

    private String resolveUserId(HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if (p != null && p.getName() != null && !p.getName().isBlank()) return p.getName().trim();

        String header = request.getHeader(props.getHeader().getUserIdHeader());
        if (header != null && !header.isBlank()) return header.trim();

        return null;
    }

    private String resolveClientIp(HttpServletRequest request) {
        // 프록시 환경에서 가장 흔한 헤더
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
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
