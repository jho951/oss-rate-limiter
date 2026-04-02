# rate-limiter-core

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-core)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../LICENSE)

판정 엔진과 in-memory store를 담는 모듈이다.

상세 설명은 [docs/modules/core.md](../docs/modules/core.md)를 본다.

## Contains

- `TokenBucketRateLimiter`
- `InMemoryTokenBucketStore`
- `TokenBucketStore`
- `TokenBucketState`
- `Clock`

## Role

- `RateLimitKey`와 `RateLimitPlan`을 입력받아 허용/차단을 판정한다.
- 남은 토큰과 `retryAfterMillis`를 계산한다.
- Spring 없이 순수 Java로 실행 가능하다.

## Do Not Put Here

- HTTP Filter
- Spring Boot AutoConfiguration
- Redis 연결
- 서비스별 path 정책
