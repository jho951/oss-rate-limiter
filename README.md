# ratelimiter (Public)

IP / 사용자 ID / API 키 기준으로 요청 빈도를 제한(rate limiting)하는 **재사용 가능한 Public 모듈**입니다.

- v1: **In-Memory Token Bucket** (단일 인스턴스 기준)
- Spring Boot / 순수 Java 모두 사용 가능
- Spring Boot에서는 `ratelimiter-config` 의존성만 추가하면 Filter로 자동 적용됩니다.

## Module Layout

- `ratelimiter-api`  : 외부에 노출되는 공개 타입(API)
- `ratelimiter-core` : 알고리즘(Token Bucket) + in-memory store
- `ratelimiter-config`: Spring Boot AutoConfiguration + Filter + Properties

## Quick Start (Spring Boot)

### Gradle
```gradle
dependencies {
  implementation("io.github.jho951:ratelimiter-config:0.1.0")
}
```

### application.yml
```yaml
ratelimiter:
  enabled: true
  header:
    api-key-header: X-API-Key
    user-id-header: X-User-Id
  ip:
    capacity: 60
    refill-per-second: 60
  user-id:
    capacity: 120
    refill-per-second: 120
  api-key:
    capacity: 300
    refill-per-second: 300
```

- 기본은 **IP → USER_ID → API_KEY** 순으로 각각 1 토큰씩 차감합니다.
- 429 응답 시 `Retry-After` 헤더(초)를 함께 내려줍니다.

## Quick Start (Pure Java)

```java
import com.ratelimiter.api.*;
import com.ratelimiter.core.TokenBucketRateLimiter;

public class Demo {
  public static void main(String[] args) {
    TokenBucketRateLimiter limiter = new TokenBucketRateLimiter();
    RateLimitPlan plan = new RateLimitPlan(10, 1); // capacity=10, refill=1/sec

    RateLimitKey key = RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4");

    for (int i = 0; i < 20; i++) {
      RateLimitDecision d = limiter.tryAcquire(key, 1, plan);
      System.out.println(i + " allowed=" + d.isAllowed()
          + " remaining=" + d.getRemainingTokens()
          + " retryAfterMs=" + d.getRetryAfterMillis());
    }
  }
}
```

## Notes (v1 Limitations)

- In-memory 기반이라 **다중 서버(분산) 환경에서는 인스턴스별로 제한이 따로 잡힙니다.**
- v2에서는 Redis/DB 기반 Store로 확장하는 것을 권장합니다.
