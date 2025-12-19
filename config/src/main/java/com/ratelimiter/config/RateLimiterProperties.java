package com.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ratelimiter.* 설정 바인딩
 */
@ConfigurationProperties(prefix = "ratelimiter")
public class RateLimiterProperties {

    private boolean enabled = true;

    private Header header = new Header();
    private Plan ip = new Plan(60, 60);
    private Plan userId = new Plan(120, 120);
    private Plan apiKey = new Plan(300, 300);

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }

    public Plan getIp() { return ip; }
    public void setIp(Plan ip) { this.ip = ip; }

    public Plan getUserId() { return userId; }
    public void setUserId(Plan userId) { this.userId = userId; }

    public Plan getApiKey() { return apiKey; }
    public void setApiKey(Plan apiKey) { this.apiKey = apiKey; }

    public static class Header {
        private String apiKeyHeader = "X-API-Key";
        private String userIdHeader = "X-User-Id";

        public String getApiKeyHeader() { return apiKeyHeader; }
        public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }

        public String getUserIdHeader() { return userIdHeader; }
        public void setUserIdHeader(String userIdHeader) { this.userIdHeader = userIdHeader; }
    }

    public static class Plan {
        private long capacity;
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
}
