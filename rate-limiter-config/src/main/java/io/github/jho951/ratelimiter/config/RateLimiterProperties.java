package io.github.jho951.ratelimiter.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** 설정 바인딩*/
@Validated
@ConfigurationProperties(prefix = "ratelimiter")
public class RateLimiterProperties {

    private boolean enabled = true;
    private String mode = "memory";
    private boolean trustForwardHeaders = false;
    private boolean failOpen = false;

    @Valid
    private Header header = new Header();
    @Valid
    private Plan ip = new Plan(60, 60);
    @Valid
    private Plan userId = new Plan(120, 120);
    @Valid
    private Plan apiKey = new Plan(300, 300);
    @Valid
    private Redis redis = new Redis();
    private List<String> excludedPaths = new ArrayList<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public boolean isTrustForwardHeaders() { return trustForwardHeaders; }
    public void setTrustForwardHeaders(boolean trustForwardHeaders) { this.trustForwardHeaders = trustForwardHeaders; }

    public boolean isFailOpen() { return failOpen; }
    public void setFailOpen(boolean failOpen) { this.failOpen = failOpen; }

    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }

    public Plan getIp() { return ip; }
    public void setIp(Plan ip) { this.ip = ip; }

    public Plan getUserId() { return userId; }
    public void setUserId(Plan userId) { this.userId = userId; }

    public Plan getApiKey() { return apiKey; }
    public void setApiKey(Plan apiKey) { this.apiKey = apiKey; }

    public Redis getRedis() { return redis; }
    public void setRedis(Redis redis) { this.redis = redis; }

    public List<String> getExcludedPaths() { return excludedPaths; }
    public void setExcludedPaths(List<String> excludedPaths) { this.excludedPaths = excludedPaths; }

    public static class Header {
        @NotBlank
        private String apiKeyHeader = "X-API-Key";
        @NotBlank
        private String userIdHeader = "X-User-Id";

        public String getApiKeyHeader() { return apiKeyHeader; }
        public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }

        public String getUserIdHeader() { return userIdHeader; }
        public void setUserIdHeader(String userIdHeader) { this.userIdHeader = userIdHeader; }
    }

    public static class Plan {
        @Positive
        private long capacity;
        @Positive
        private double refillPerSecond;

        public Plan() {}

        public Plan(long capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
        }

        public long getCapacity() { return capacity; }
        public void setCapacity(long capacity) { this.capacity = capacity; }

        public double getRefillPerSecond() { return refillPerSecond; }
        public void setRefillPerSecond(double refillPerSecond) { this.refillPerSecond = refillPerSecond; }
    }

    public static class Redis {
        @NotBlank
        private String keyPrefix = "ratelimiter:";

        public String getKeyPrefix() { return keyPrefix; }

        public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
    }
}
