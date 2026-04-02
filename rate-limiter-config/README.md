# rate-limiter-config

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Publish](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-config?label=maven%20central)](https://central.sonatype.com/search?q=io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/rate-limiter)](../LICENSE)

이 모듈은 Spring Boot adapter 레이어입니다.

- HTTP 요청에서 key와 policy를 추출
- core 또는 redis 구현을 호출
- `429` 응답과 `Retry-After` 헤더를 처리
- 범용 설정과 override 지점을 제공

상세 설명은 [docs/modules/config.md](../docs/modules/config.md)를 참고하세요.

## 30초 요약

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-config:1.0.0")
}
```

```yml
ratelimiter:
  enabled: true
  mode: memory
  trust-forward-headers: false
  fail-open: false
  excluded-paths:
    - /health
```

## 포함 타입

- `RateLimiterAutoConfiguration`
- `RateLimitingFilter`
- `RateLimiterProperties`
- `DefaultHttpRateLimitKeyResolver`
- `DefaultHttpRateLimitPolicyResolver`

## 역할

- HTTP 요청에서 `RateLimitKey`를 만듭니다.
- 기본 `RateLimitPolicy` 목록을 생성합니다.
- `429` 응답과 `Retry-After` 헤더를 처리합니다.
- `excluded-paths`, `trust-forward-headers`, `fail-open` 같은 범용 설정을 제공합니다.
- `RateLimitKeyResolver`와 `RateLimitPolicyResolver`를 bean으로 교체할 수 있게 합니다.

## 설정 그룹

- `enabled`: 전체 기능 on/off
- `mode`: `memory` 또는 `redis`
- `header`: API key, user id 헤더 이름
- `ip`, `user-id`, `api-key`: 기본 capacity / refill rate
- `excluded-paths`: rate limiting 제외 경로
- `trust-forward-headers`: `X-Forwarded-For` 신뢰 여부
- `fail-open`: rate limiting 실패 시 통과 여부

## 하지 않는 것

- Token Bucket 알고리즘
- Redis script
- 서비스별 business quota
- 권한 판단

## 관련 문서

- [Architecture](../docs/architecture.md)
- [Config Docs](../docs/modules/config.md)
