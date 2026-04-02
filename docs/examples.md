# Examples

공용 예시만 이 문서에 모아둔다.

## Java

```java
import io.github.jho951.ratelimiter.api.RateLimitDecision;
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;
import io.github.jho951.ratelimiter.api.RateLimitPlan;
import io.github.jho951.ratelimiter.core.TokenBucketRateLimiter;

TokenBucketRateLimiter limiter = new TokenBucketRateLimiter();
RateLimitDecision decision = limiter.tryAcquire(
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    1,
    new RateLimitPlan(10, 1)
);
```

## Spring Boot

### Configuration prefix

`ratelimiter:`는 모듈명이 아니라 Spring 설정 prefix다.

```gradle
dependencies {
  implementation("io.github.jho951:rate-limiter-config:1.1.0")
  implementation("io.github.jho951:rate-limiter-redis:1.1.0") // optional
}
```

```yaml
ratelimiter:
  enabled: true
  mode: memory
  trust-forward-headers: false
  fail-open: false
  excluded-paths:
    - /actuator/**
```

```yaml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter:
```
