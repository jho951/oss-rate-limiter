# ratelimiter (Public)

IP / 사용자 ID / API 키 기준으로 요청 빈도를 제한(rate limiting)하는 **재사용 가능한 Public 모듈**입니다.

- v1: **In-Memory Token Bucket** (단일 인스턴스 기준)
- v2: **Redis Token Bucket** (분산 환경 기준)
- Spring Boot / 순수 Java 모두 사용 가능
- Spring Boot에서는 `ratelimiter-config` 의존성만 추가하면 Filter로 자동 적용됩니다.
- Maven Central 배포용 메타데이터와 서명 설정이 포함되어 있습니다.

## Module Layout

- `ratelimiter-api`  : 외부에 노출되는 공개 타입(API)
- `ratelimiter-core` : 알고리즘(Token Bucket) + in-memory store
- `ratelimiter-config`: Spring Boot AutoConfiguration + Filter + Properties
- `ratelimiter-redis` : Redis 기반 분산 RateLimiter

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
  mode: memory
  trust-forward-headers: false
  fail-open: false
  excluded-paths:
    - /actuator/**
    - /health
```

- 기본은 **IP → USER_ID → API_KEY** 순으로 각각 1 토큰씩 차감합니다.
- 429 응답 시 `Retry-After` 헤더(초)를 함께 내려줍니다.
- `trust-forward-headers=true`일 때만 `X-Forwarded-For`를 신뢰합니다.
- `excluded-paths`에 매칭되는 경로는 rate limiting을 적용하지 않습니다.
- Redis 분산 모드를 쓰려면 `ratelimiter-redis` 의존성을 추가하고 `mode: redis`로 바꾸면 됩니다.
- `RateLimitKeyResolver` / `RateLimitPolicyResolver` Bean을 직접 정의하면 기본 추출/정책을 교체할 수 있습니다.

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

## Maven Central Publish

이 저장소는 Central Portal 기준으로 발행하도록 설정되어 있습니다.

GitHub Actions 발행:

- `v1.2.3` 형태의 태그를 push하면 `.github/workflows/publish.yml`가 실행됩니다.
- 워크플로는 `publishAndReleaseToMavenCentral`를 호출합니다.

필요한 GitHub Secrets:

- `MAVEN_CENTRAL_NAMESPACE`
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY`
- `MAVEN_CENTRAL_GPG_PASSPHRASE`

로컬 수동 발행:

```bash
./gradlew -PVERSION_NAME=1.2.3 publishAndReleaseToMavenCentral
```
