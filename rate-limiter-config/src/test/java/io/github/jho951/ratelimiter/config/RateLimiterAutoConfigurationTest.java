package io.github.jho951.ratelimiter.config;

import io.github.jho951.ratelimiter.api.RateLimiter;
import io.github.jho951.ratelimiter.core.TokenBucketStore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RateLimiterAutoConfiguration.class));

    @Test
    void createsDefaultBeans() {
        contextRunner
            .withPropertyValues("ratelimiter.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(TokenBucketStore.class);
                assertThat(context).hasSingleBean(RateLimiter.class);
                assertThat(context).hasSingleBean(RateLimitingFilter.class);
            });
    }

    @Test
    void validatesInvalidConfiguration() {
        contextRunner
            .withPropertyValues(
                "ratelimiter.enabled=true",
                "ratelimiter.ip.capacity=0",
                "ratelimiter.ip.refill-per-second=60"
            )
            .run(context -> assertThat(context.getStartupFailure()).isNotNull());
    }
}
