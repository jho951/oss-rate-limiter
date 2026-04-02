# Examples

이 문서는 공용 예시를 모아둔 빠른 참고용 문서입니다.

## 순수 Java

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
    RateLimitPlan.perSecond(10, 1.0)
);
```

## Spring Boot

### 의존성

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-config:1.0.0")
    implementation("io.github.jho951:rate-limiter-redis:1.0.0") // optional
}
```

### 기본 설정

```yaml
ratelimiter:
  enabled: true
  mode: memory
  trust-forward-headers: false
  fail-open: false
  excluded-paths:
    - /actuator/**
```

### Redis 모드

```yaml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter:
```

## Key Resolver 커스터마이징

```java
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyResolver;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;
import jakarta.servlet.http.HttpServletRequest;

RateLimitKeyResolver<HttpServletRequest> resolver =
    (request, type) -> RateLimitKey.of(type, "custom-value");
```

## Policy 예시

```java
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;
import io.github.jho951.ratelimiter.api.RateLimitPlan;
import io.github.jho951.ratelimiter.api.RateLimitPolicy;

RateLimitPolicy policy = RateLimitPolicy.of(
    "ip-limit",
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    RateLimitPlan.perSecond(60, 60.0),
    1
);
```

## 참고

- API 계약: [docs/modules/api.md](./modules/api.md)
- Core 동작: [docs/modules/core.md](./modules/core.md)
- Config 설정: [docs/modules/config.md](./modules/config.md)
- Redis 확장: [docs/modules/redis.md](./modules/redis.md)
