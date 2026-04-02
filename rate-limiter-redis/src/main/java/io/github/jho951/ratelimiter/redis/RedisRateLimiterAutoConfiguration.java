package io.github.jho951.ratelimiter.redis;

import io.github.jho951.ratelimiter.api.RateLimiter;
import io.github.jho951.ratelimiter.config.RateLimiterProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 기반 분산 RateLimiter auto configuration.
 */
@AutoConfiguration
@ConditionalOnClass({RedisConnectionFactory.class, StringRedisTemplate.class})
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = "ratelimiter", name = "mode", havingValue = "redis")
public class RedisRateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(RateLimiter.class)
    public RateLimiter redisRateLimiter(StringRedisTemplate stringRedisTemplate, RateLimiterProperties properties) {
        return new RedisTokenBucketRateLimiter(stringRedisTemplate, properties);
    }
}
