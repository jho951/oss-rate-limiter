package com.ratelimiter.redis;

import com.ratelimiter.api.RateLimitDecision;
import com.ratelimiter.api.RateLimitKey;
import com.ratelimiter.api.RateLimitPlan;
import com.ratelimiter.api.RateLimiter;
import com.ratelimiter.config.RateLimiterProperties;
import com.ratelimiter.core.Clock;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Redis 기반 분산 Token Bucket RateLimiter.
 */
public final class RedisTokenBucketRateLimiter implements RateLimiter {

    private static final RedisScript<List> SCRIPT = buildScript();

    private final StringRedisTemplate redisTemplate;
    private final Clock clock;
    private final RateLimiterProperties properties;

    public RedisTokenBucketRateLimiter(StringRedisTemplate redisTemplate, RateLimiterProperties properties) {
        this(redisTemplate, Clock.system(), properties);
    }

    public RedisTokenBucketRateLimiter(StringRedisTemplate redisTemplate, Clock clock, RateLimiterProperties properties) {
        this.redisTemplate = redisTemplate;
        this.clock = clock;
        this.properties = properties;
    }

    @Override
    public RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        if (plan == null) throw new IllegalArgumentException("plan must not be null");
        if (permits <= 0) permits = 1;

        String redisKey = properties.getRedis().getKeyPrefix() + key.asString();
        long now = clock.nanoTime();
        long ttlMillis = computeTtlMillis(plan);

        List<?> result = redisTemplate.execute(
            SCRIPT,
            Collections.singletonList(redisKey),
            String.valueOf(now),
            String.valueOf(plan.getCapacity()),
            String.valueOf(plan.getRefillTokensPerSecond()),
            String.valueOf(permits),
            String.valueOf(ttlMillis)
        );

        if (result == null || result.size() < 3) {
            throw new IllegalStateException("redis rate limiter returned no result");
        }

        boolean allowed = asLong(result.get(0)) == 1L;
        long remaining = asLong(result.get(1));
        long retryAfterMillis = asLong(result.get(2));
        return allowed ? RateLimitDecision.allow(remaining) : RateLimitDecision.deny(remaining, retryAfterMillis);
    }

    private long computeTtlMillis(RateLimitPlan plan) {
        double refillPerSecond = plan.getRefillTokensPerSecond();
        double fullRefillSeconds = plan.getCapacity() / refillPerSecond;
        long ttlMillis = (long) Math.ceil(fullRefillSeconds * 1000.0 * 2.0);
        return Math.max(1000L, ttlMillis);
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static RedisScript<List> buildScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setResultType(List.class);
        script.setScriptText("""
            local key = KEYS[1]
            local now = tonumber(ARGV[1])
            local capacity = tonumber(ARGV[2])
            local refillPerSecond = tonumber(ARGV[3])
            local permits = tonumber(ARGV[4])
            local ttlMillis = tonumber(ARGV[5])

            local tokens = tonumber(redis.call("HGET", key, "tokens"))
            local lastRefillNanos = tonumber(redis.call("HGET", key, "lastRefillNanos"))

            if tokens == nil then
              tokens = capacity
            end

            if lastRefillNanos == nil then
              lastRefillNanos = now
            end

            local elapsedNanos = now - lastRefillNanos
            if elapsedNanos > 0 then
              local elapsedSeconds = elapsedNanos / 1000000000.0
              local refill = elapsedSeconds * refillPerSecond
              tokens = math.min(capacity, tokens + refill)
              lastRefillNanos = now
            end

            local allowed = 0
            local retryAfterMillis = 0

            if tokens >= permits then
              tokens = tokens - permits
              allowed = 1
            else
              local need = permits - tokens
              local seconds = need / refillPerSecond
              retryAfterMillis = math.ceil(seconds * 1000.0)
            end

            redis.call("HSET", key, "tokens", tokens, "lastRefillNanos", lastRefillNanos)
            redis.call("PEXPIRE", key, ttlMillis)

            return { allowed, math.floor(tokens), retryAfterMillis }
            """);
        return script;
    }
}
