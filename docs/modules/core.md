# rate-limiter-core

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-core)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../../LICENSE)

## Role

`rate-limiter-core`는 판정 엔진이다.

이 모듈은 key와 plan을 받아 허용/차단을 계산한다.

Spring, HTTP, Redis에 의존하지 않는 알고리즘 본체다.

## Responsibilities

- Token Bucket 계산
- key별 상태 저장
- refill 계산
- remaining token 계산
- retry-after 계산

## Execution Model

- `TokenBucketRateLimiter`가 진입점이다.
- `TokenBucketStore`가 상태 저장소 계약이다.
- `InMemoryTokenBucketStore`가 기본 구현체다.
- `Clock`은 테스트 가능성을 위한 시간 추상화다.
- `TokenBucketState`는 key별 버킷 상태를 담는다.

## Behavioral Notes

- key마다 독립적인 버킷을 유지한다.
- 요청 수가 capacity를 넘으면 deny 한다.
- refill rate에 따라 시간이 지나면 토큰이 다시 채워진다.
- `permits <= 0`은 1로 정규화한다.
- `retryAfterMillis`는 재시도 가능 시점을 계산하기 위한 최소 힌트다.

## Main Types

- `TokenBucketRateLimiter`
- `InMemoryTokenBucketStore`
- `TokenBucketStore`
- `TokenBucketState`
- `Clock`

## Example

```java
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;
import io.github.jho951.ratelimiter.api.RateLimitPlan;
import io.github.jho951.ratelimiter.core.TokenBucketRateLimiter;

TokenBucketRateLimiter limiter = new TokenBucketRateLimiter();
limiter.tryAcquire(
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    1,
    new RateLimitPlan(10, 1)
);
```

## What Belongs Here

- 알고리즘
- 상태 저장
- 순수 Java 실행 가능한 로직

## What Does Not Belong Here

- HTTP filter
- Spring Boot auto configuration
- Redis script
- 서비스별 path 정책

## Related Docs

- [Architecture](../architecture.md)
- [Examples](../examples.md)
