# rate-limiter-api

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-api)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../LICENSE)

공개 계약만 담는 모듈이다.

상세 설명은 [docs/modules/api.md](../docs/modules/api.md)를 본다.

## Contains

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimiter`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`
- `RateLimitException`

## Role

- 다른 모듈이 의존하는 안정적인 API를 제공한다.
- Spring, Redis, storage 구현에 종속되지 않는다.
- 순수 Java 코드에서도 바로 사용할 수 있다.

## Do Not Put Here

- 알고리즘 구현
- Spring Boot 설정
- Redis 연동 코드
- 서비스별 정책 하드코딩
