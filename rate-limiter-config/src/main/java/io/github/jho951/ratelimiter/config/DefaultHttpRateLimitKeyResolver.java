package io.github.jho951.ratelimiter.config;

import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyResolver;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

/**
 * HttpServletRequest 기준 기본 키 추출기.
 */
class DefaultHttpRateLimitKeyResolver implements RateLimitKeyResolver<HttpServletRequest> {

    private final RateLimiterProperties props;

    DefaultHttpRateLimitKeyResolver(RateLimiterProperties props) {
        this.props = props;
    }

    @Override
    public RateLimitKey resolve(HttpServletRequest request, RateLimitKeyType type) {
        return switch (type) {
            case IP -> resolveClientIp(request);
            case USER_ID -> resolveUserId(request);
            case API_KEY -> resolveApiKey(request);
        };
    }

    private RateLimitKey resolveClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (props.isTrustForwardHeaders()) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                ip = xff.split(",")[0].trim();
            }
        }
        if (ip == null || ip.isBlank()) {
            return null;
        }
        return RateLimitKey.of(RateLimitKeyType.IP, ip.trim());
    }

    private RateLimitKey resolveUserId(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
            return RateLimitKey.of(RateLimitKeyType.USER_ID, principal.getName().trim());
        }

        String header = request.getHeader(props.getHeader().getUserIdHeader());
        if (header == null || header.isBlank()) {
            return null;
        }
        return RateLimitKey.of(RateLimitKeyType.USER_ID, header.trim());
    }

    private RateLimitKey resolveApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(props.getHeader().getApiKeyHeader());
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        return RateLimitKey.of(RateLimitKeyType.API_KEY, apiKey.trim());
    }
}
