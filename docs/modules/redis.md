# rate-limiter-redis

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-redis)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../../LICENSE)

## Role

`rate-limiter-redis`는 분산 환경용 저장 구현이다.

단일 인스턴스가 아니라 여러 인스턴스가 같은 버킷 상태를 공유해야 할 때 사용한다.

이 모듈은 OSS에 포함될 수 있지만, 핵심 알고리즘이 아니라 선택 확장이다.

## Responsibilities

- Redis를 상태 저장소로 사용한다.
- Lua script로 원자적 판정을 수행한다.
- `mode: redis`일 때만 활성화된다.

## Runtime Behavior

- `RedisTokenBucketRateLimiter`가 Redis를 통해 토큰 상태를 읽고 쓴다.
- Lua script로 읽기/계산/쓰기 과정을 원자적으로 묶는다.
- `RateLimiterProperties.redis.key-prefix`로 키 접두사를 제어한다.
- `mode=redis`일 때만 auto configuration이 활성화된다.

## Main Types

- `RedisTokenBucketRateLimiter`
- `RedisRateLimiterAutoConfiguration`

## Example

```yaml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter:
```

```java
// Redis implementation is activated by Spring Boot auto-configuration.
```

## What Belongs Here

- 분산 저장
- 원자적 갱신
- Redis 전용 backend

## What Does Not Belong Here

- 서비스별 정책 하드코딩
- 인증 엔진
- 운영 콘솔
- 과금 정책

## Related Docs

- [Architecture](../architecture.md)
- [Examples](../examples.md)
