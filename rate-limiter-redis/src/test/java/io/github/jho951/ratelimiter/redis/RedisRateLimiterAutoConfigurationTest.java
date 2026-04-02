package io.github.jho951.ratelimiter.redis;

import io.github.jho951.ratelimiter.api.RateLimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisRateLimiterAutoConfigurationTest {

    @Test
    void createsRedisRateLimiterWhenModeIsRedis() {
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisRateLimiterAutoConfiguration.class))
            .withBean(RedisConnectionFactory.class, () -> connectionFactory)
            .withBean(StringRedisTemplate.class, () -> {
                StringRedisTemplate template = new StringRedisTemplate();
                template.setConnectionFactory(connectionFactory);
                return template;
            })
            .withPropertyValues(
                "ratelimiter.enabled=true",
                "ratelimiter.mode=redis"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(RateLimiter.class);
                assertThat(context.getBean(RateLimiter.class)).isInstanceOf(RedisTokenBucketRateLimiter.class);
            });
    }
}
