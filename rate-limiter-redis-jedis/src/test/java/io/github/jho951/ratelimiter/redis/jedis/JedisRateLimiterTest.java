package io.github.jho951.ratelimiter.redis.jedis;

import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitKeyType;
import io.github.jho951.ratelimiter.core.RateLimitPlan;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class JedisRateLimiterTest {

    @Test
    void redisKeyDoesNotContainTheOriginalApiKey() {
        JedisRateLimiter limiter = new JedisRateLimiter(new JedisPool(), "test-prefix:");
        RateLimitKey key = RateLimitKey.of(RateLimitKeyType.API_KEY, "sensitive-value");

        String redisKey = limiter.toRedisKey(key);

        assertTrue(redisKey.startsWith("test-prefix:"));
        assertFalse(redisKey.contains("sensitive-value"));
        assertEquals(redisKey, limiter.toRedisKey(key));
    }

    @Test
    void constructorRejectsBlankKeyPrefix() {
        assertThrows(IllegalArgumentException.class, () -> new JedisRateLimiter(new JedisPool(), " "));
    }

    @Test
    void concurrentRequestsCannotExceedBucketCapacity() throws Exception {
        String host = System.getenv("RATE_LIMIT_REDIS_TEST_HOST");
        assumeTrue(host != null && !host.trim().isEmpty(), "Redis integration test is not configured");
        int port = Integer.parseInt(System.getenv().getOrDefault("RATE_LIMIT_REDIS_TEST_PORT", "6379"));

        try (JedisPool pool = new JedisPool(new JedisPoolConfig(), host, port, 2000)) {
            JedisRateLimiter limiter = new JedisRateLimiter(pool, "rate-limiter-test:");
            RateLimitKey key = RateLimitKey.of(RateLimitKeyType.CUSTOM, UUID.randomUUID().toString());
            RateLimitPlan plan = RateLimitPlan.perSecond(5, 0.0001);
            ExecutorService executor = Executors.newFixedThreadPool(12);
            try {
                List<Callable<Boolean>> calls = new ArrayList<Callable<Boolean>>();
                for (int index = 0; index < 40; index++) {
                    calls.add(() -> limiter.tryAcquire(key, 1, plan).isAllowed());
                }
                List<Future<Boolean>> results = executor.invokeAll(calls);
                int allowed = 0;
                for (Future<Boolean> result : results) {
                    if (result.get()) allowed++;
                }
                assertEquals(5, allowed);
            } finally {
                executor.shutdownNow();
            }
        }
    }

    @Test
    void differentKeysHaveIndependentBuckets() throws Exception {
        String host = System.getenv("RATE_LIMIT_REDIS_TEST_HOST");
        assumeTrue(host != null && !host.trim().isEmpty(), "Redis integration test is not configured");
        int port = Integer.parseInt(System.getenv().getOrDefault("RATE_LIMIT_REDIS_TEST_PORT", "6379"));

        try (JedisPool pool = new JedisPool(new JedisPoolConfig(), host, port, 2000)) {
            JedisRateLimiter limiter = new JedisRateLimiter(pool, "rate-limiter-test:");
            RateLimitPlan plan = RateLimitPlan.perSecond(1, 0.0001);
            RateLimitKey firstKey = RateLimitKey.of(RateLimitKeyType.CUSTOM, UUID.randomUUID().toString());
            RateLimitKey secondKey = RateLimitKey.of(RateLimitKeyType.CUSTOM, UUID.randomUUID().toString());

            assertTrue(limiter.tryAcquire(firstKey, 1, plan).isAllowed());
            assertFalse(limiter.tryAcquire(firstKey, 1, plan).isAllowed());
            assertTrue(limiter.tryAcquire(secondKey, 1, plan).isAllowed());
            assertNotEquals(limiter.toRedisKey(firstKey), limiter.toRedisKey(secondKey));
        }
    }
}
