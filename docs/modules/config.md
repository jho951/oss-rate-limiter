# rate-limiter-config

이 문서는 `rate-limiter-config`의 Spring Boot adapter 역할을 설명합니다.

## 역할

`rate-limiter-config`는 HTTP 어댑테이션과 조립을 담당합니다.

- HTTP 요청에서 key와 policy를 추출
- core 또는 redis 구현을 호출
- `429` 응답과 `Retry-After` 헤더를 처리
- 범용 설정과 override 지점을 제공

## 주요 타입

- `RateLimiterAutoConfiguration`
- `RateLimitingFilter`
- `RateLimiterProperties`
- `DefaultHttpRateLimitKeyResolver`
- `DefaultHttpRateLimitPolicyResolver`

## 설정 그룹

### `enabled`

- 전체 기능 on/off 스위치입니다.

### `mode`

- `memory`는 core의 in-memory 구현을 사용합니다.
- `redis`는 분산 저장 구현을 사용합니다.

### `header`

- API key 헤더 이름을 정합니다.
- user id 헤더 이름을 정합니다.

### `ip`, `user-id`, `api-key`

- 각 key type에 대한 capacity와 refill rate를 정합니다.

### `excluded-paths`

- 특정 path를 rate limiting에서 제외합니다.

### `trust-forward-headers`

- `X-Forwarded-For` 신뢰 여부를 결정합니다.

### `fail-open`

- rate limiting 실패 시 요청 통과 여부를 정합니다.

## Override Points

- `RateLimitKeyResolver<HttpServletRequest>`
- `RateLimitPolicyResolver<HttpServletRequest>`

이 둘을 bean으로 교체하면 기본 추출과 정책 계산을 바꿀 수 있습니다.

## 예시

```yaml
ratelimiter:
  enabled: true
  mode: memory
  trust-forward-headers: false
  fail-open: false
  excluded-paths:
    - /health
```

```java
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyResolver;
import jakarta.servlet.http.HttpServletRequest;

RateLimitKeyResolver<HttpServletRequest> resolver =
    (request, type) -> RateLimitKey.of(type, "custom");
```

## 하지 않는 것

- Token Bucket 구현
- Redis 저장 로직
- 도메인별 quota
- 권한 판단

## 관련 문서

- [Architecture](../architecture.md)
- [Examples](../examples.md)
