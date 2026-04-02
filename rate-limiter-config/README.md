# rate-limiter-config

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-config)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../LICENSE)

Spring Boot adapter 모듈이다.

상세 설명은 [docs/modules/config.md](../docs/modules/config.md)를 본다.

## Contains

- `RateLimiterAutoConfiguration`
- `RateLimitingFilter`
- `RateLimiterProperties`
- `DefaultHttpRateLimitKeyResolver`
- `DefaultHttpRateLimitPolicyResolver`

## Role

- HTTP 요청에서 `RateLimitKey`를 만든다.
- 기본 `RateLimitPlan`을 적용한다.
- `429` 응답과 `Retry-After` 헤더를 처리한다.
- `excluded-paths`, `trust-forward-headers`, `fail-open` 같은 범용 설정을 제공한다.
- `RateLimitKeyResolver` / `RateLimitPolicyResolver` override 지점을 제공한다.

## Do Not Put Here

- Token Bucket 알고리즘
- Redis script
- 서비스별 business quota
- 권한 판단
