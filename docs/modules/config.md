# rate-limiter-config

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-config)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../../LICENSE)

## Role

`rate-limiter-config`는 Spring Boot adapter다.

HTTP 요청에서 key와 policy를 만들고, core나 redis 구현을 호출한다.

이 모듈은 조립과 HTTP 어댑테이션을 담당한다.

## Responsibilities

- AutoConfiguration 제공
- Filter 제공
- Properties 바인딩 제공
- 기본 HTTP key 추출 제공
- 기본 정책 추출 제공
- 429 응답 제공
- Retry-After 헤더 제공

## Configuration Groups

### `enabled`

- 전체 기능 on/off 스위치다.

### `mode`

- `memory`는 core의 in-memory 구현을 쓴다.
- `redis`는 분산 저장 구현을 쓴다.

### `header`

- API key 헤더 이름을 정한다.
- user id 헤더 이름을 정한다.

### `ip`, `user-id`, `api-key`

- 각 key type에 대한 capacity와 refill rate를 정한다.

### `excluded-paths`

- 특정 path를 rate limiting에서 제외한다.

### `trust-forward-headers`

- `X-Forwarded-For` 신뢰 여부를 결정한다.

### `fail-open`

- rate limiting 실패 시 요청 통과 여부를 정한다.

## Override Points

- `RateLimitKeyResolver<HttpServletRequest>`
- `RateLimitPolicyResolver<HttpServletRequest>`

이 둘을 bean으로 교체하면 기본 추출과 정책 계산을 바꿀 수 있다.

## Main Types

- `RateLimiterAutoConfiguration`
- `RateLimitingFilter`
- `RateLimiterProperties`
- `DefaultHttpRateLimitKeyResolver`
- `DefaultHttpRateLimitPolicyResolver`

## Example

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

## What Belongs Here

- Spring Boot wiring
- HTTP adapter
- 범용 설정

## What Does Not Belong Here

- Token Bucket 구현
- Redis 저장 로직
- 도메인별 quota
- 권한 판단

## Related Docs

- [Architecture](../architecture.md)
- [Examples](../examples.md)
