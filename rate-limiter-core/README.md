# rate-limiter-core

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Publish](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-core?label=maven%20central)](https://central.sonatype.com/search?q=io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/rate-limiter)](../LICENSE)

이 모듈은 Token Bucket 판정 엔진과 in-memory store를 담는 core 레이어입니다.

- key 별 버킷 상태를 관리
- 허용 / 차단 여부를 계산
- remaining token과 retry-after를 계산
- Spring 없이 순수 Java로 실행 가능

상세 설명은 [docs/modules/core.md](../docs/modules/core.md)를 참고하세요.

## 30초 요약

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-core:1.0.0")
}
```

```java
RateLimiter limiter = new TokenBucketRateLimiter();
RateLimitDecision decision = limiter.tryAcquire(
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    1,
    RateLimitPlan.perSecond(10, 1.0)
);
```

## 포함 타입

- `TokenBucketRateLimiter`
- `InMemoryTokenBucketStore`
- `TokenBucketStore`
- `TokenBucketState`
- `Clock`

## 역할

- `RateLimitKey`와 `RateLimitPlan`을 입력받아 허용/차단을 판정합니다.
- 남은 토큰과 `retryAfterMillis`를 계산합니다.
- key 별로 독립적인 버킷 상태를 유지합니다.
- `Clock`을 통해 테스트 가능한 시간을 제공합니다.

## 설계 원칙

- 알고리즘만 담당합니다.
- HTTP, Spring Boot, Redis 구현 세부사항은 포함하지 않습니다.
- 순수 Java 환경에서도 재사용할 수 있어야 합니다.

## 하지 않는 것

- HTTP Filter
- Spring Boot AutoConfiguration
- Redis 연결
- 서비스별 path 정책

## 관련 문서

- [Architecture](../docs/architecture.md)
- [Core Docs](../docs/modules/core.md)
