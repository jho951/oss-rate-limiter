package io.github.jho951.ratelimiter.redis.jedis;

import io.github.jho951.ratelimiter.core.RateLimitDecision;
import io.github.jho951.ratelimiter.core.RateLimitKey;
import io.github.jho951.ratelimiter.core.RateLimitPlan;
import io.github.jho951.ratelimiter.spi.RateLimiter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Jedis를 사용해 Redis에서 원자적으로 토큰 버킷을 처리하는 구현체입니다. */
public final class JedisRateLimiter implements RateLimiter {

    private static final String DEFAULT_KEY_PREFIX = "rate-limiter:";
    private static final String TOKEN_BUCKET_SCRIPT =
        "local capacity = tonumber(ARGV[1])\n" +
        "local refillPerSecond = tonumber(ARGV[2])\n" +
        "local permits = tonumber(ARGV[3])\n" +
        "local ttlMillis = tonumber(ARGV[4])\n" +
        "local time = redis.call('TIME')\n" +
        "local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000)\n" +
        "local values = redis.call('HMGET', KEYS[1], 'tokens', 'updatedAt')\n" +
        "local tokens = tonumber(values[1])\n" +
        "local updatedAt = tonumber(values[2])\n" +
        "if tokens == nil then tokens = capacity end\n" +
        "if updatedAt == nil then updatedAt = now end\n" +
        "local effectiveNow = math.max(now, updatedAt)\n" +
        "local elapsed = effectiveNow - updatedAt\n" +
        "tokens = math.min(capacity, tokens + elapsed * refillPerSecond / 1000)\n" +
        "local allowed = 0\n" +
        "local retryAfterMillis = 0\n" +
        "if tokens >= permits then\n" +
        "  allowed = 1\n" +
        "  tokens = tokens - permits\n" +
        "else\n" +
        "  retryAfterMillis = math.ceil((permits - tokens) * 1000 / refillPerSecond)\n" +
        "end\n" +
        "redis.call('HSET', KEYS[1], 'tokens', tokens, 'updatedAt', effectiveNow)\n" +
        "redis.call('PEXPIRE', KEYS[1], ttlMillis)\n" +
        "return {allowed, math.floor(tokens), retryAfterMillis}";

    private final JedisPool jedisPool;
    private final String keyPrefix;

    /** 기본 키 접두어를 사용하는 rate limiter를 생성합니다.
     * @param jedisPool Redis 연결 풀
     * @throws NullPointerException 연결 풀이 {@code null}인 경우
     */
    public JedisRateLimiter(JedisPool jedisPool) {
        this(jedisPool, DEFAULT_KEY_PREFIX);
    }

    /** 지정한 Redis 키 접두어를 사용하는 rate limiter를 생성합니다.
     * @param jedisPool Redis 연결 풀
     * @param keyPrefix Redis 키에 사용할 접두어
     * @throws NullPointerException 연결 풀 또는 접두어가 {@code null}인 경우
     * @throws IllegalArgumentException 접두어가 공백인 경우
     */
    public JedisRateLimiter(JedisPool jedisPool, String keyPrefix) {
        this.jedisPool = Objects.requireNonNull(jedisPool, "jedisPool");
        this.keyPrefix = Objects.requireNonNull(keyPrefix, "keyPrefix");
        if (keyPrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("keyPrefix is blank");
        }
    }

    /** 지정한 키의 토큰 버킷에서 요청 토큰을 원자적으로 획득합니다.
     * @param key 토큰 버킷을 구분할 대상 키
     * @param permits 획득할 토큰 수
     * @param plan 토큰 버킷 용량과 충전 계획
     * @return 허용 또는 거부 판정
     * @throws NullPointerException 필수 인수가 {@code null}인 경우
     * @throws IllegalArgumentException 토큰 수가 양수가 아니거나 버킷 용량보다 큰 경우
     */
    @Override
    public RateLimitDecision tryAcquire(RateLimitKey key, long permits, RateLimitPlan plan) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(plan, "plan");
        if (permits <= 0) {
            throw new IllegalArgumentException("permits must be > 0");
        }
        if (permits > plan.getCapacity()) {
            throw new IllegalArgumentException("permits must not exceed plan capacity");
        }

        String redisKey = toRedisKey(key);
        List<String> keys = Collections.singletonList(redisKey);
        List<String> arguments = Arrays.asList(
            Long.toString(plan.getCapacity()),
            Double.toString(plan.getRefillTokensPerSecond()),
            Long.toString(permits),
            Long.toString(ttlMillis(plan))
        );

        Object result;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.eval(TOKEN_BUCKET_SCRIPT, keys, arguments);
        }

        if (!(result instanceof List)) {
            throw new IllegalStateException("Unexpected Redis token bucket response");
        }
        List<?> values = (List<?>) result;
        if (values.size() != 3) {
            throw new IllegalStateException("Unexpected Redis token bucket response size");
        }

        long allowed = numberValue(values.get(0));
        long remainingTokens = numberValue(values.get(1));
        long retryAfterMillis = numberValue(values.get(2));
        return allowed == 1
            ? RateLimitDecision.allow(remainingTokens)
            : RateLimitDecision.deny(remainingTokens, retryAfterMillis);
    }

    String toRedisKey(RateLimitKey key) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(key.asString().getBytes(StandardCharsets.UTF_8));
            StringBuilder encoded = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                encoded.append(Character.forDigit((value >>> 4) & 0x0f, 16));
                encoded.append(Character.forDigit(value & 0x0f, 16));
            }
            return keyPrefix + encoded;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private static long numberValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Unexpected Redis token bucket value");
    }

    private static long ttlMillis(RateLimitPlan plan) {
        double refillDurationMillis = plan.getCapacity()
            / plan.getRefillTokensPerSecond() * 1000.0;
        if (Double.isInfinite(refillDurationMillis) || refillDurationMillis >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.max(1000L, (long) Math.ceil(refillDurationMillis));
    }
}
