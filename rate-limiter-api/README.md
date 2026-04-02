# rate-limiter-api

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Publish](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-api?label=maven%20central)](https://central.sonatype.com/search?q=io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/rate-limiter)](../LICENSE)

이 모듈은 외부에 노출되는 공개 계약만 담는 API 레이어입니다.

- rate limit 대상과 정책을 표현
- 판정 결과를 표현
- key / policy resolver 계약을 제공
- Spring, Redis, Servlet 구현에 종속되지 않는 순수 타입만 유지

상세 설명은 [docs/modules/api.md](../docs/modules/api.md)를 참고하세요.

## 30초 요약

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-api:1.0.0")
}
```

```java
RateLimitKey key = RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4");
RateLimitPlan plan = RateLimitPlan.perSecond(10, 1.0);
RateLimitPolicy policy = RateLimitPolicy.of("ip-limit", key, plan, 1);
```

## 포함 타입

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimitPolicy`
- `RateLimiter`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`
- `RateLimitException`

## 역할

- 다른 모듈이 의존하는 안정적인 경계를 제공합니다.
- HTTP, Spring Boot, Redis, 저장소 구현과 분리됩니다.
- 순수 Java 코드에서도 바로 사용할 수 있습니다.
- `RateLimitPolicy`로 key, plan, permits를 하나의 검사 단위로 묶습니다.

## 설계 원칙

- 데이터와 인터페이스만 둡니다.
- 구현 전략은 `rate-limiter-core`, `rate-limiter-config`, `rate-limiter-redis`가 담당합니다.
- public 타입은 작고 명확하게 유지합니다.

## 하지 않는 것

- 알고리즘 구현
- Spring Boot 설정
- Redis 연동 코드
- 서비스별 정책 하드코딩

## 관련 문서

- [Architecture](../docs/architecture.md)
- [API Docs](../docs/modules/api.md)
