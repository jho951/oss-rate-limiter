# rate-limiter-api

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-api)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../../LICENSE)

## Role

`rate-limiter-api`는 외부에 노출되는 공개 계약만 담는다.

이 모듈은 다른 모듈이 의존하는 안정적인 경계다.

Spring, Redis, Servlet, storage 구현과 무관한 타입만 둔다.

## Responsibilities

- rate limit 대상 식별자를 표현한다.
- rate limit 정책을 표현한다.
- 판정 결과를 표현한다.
- key resolver와 policy resolver의 계약을 제공한다.

## Design Rules

- 순수 데이터와 인터페이스만 둔다.
- 구현 전략은 `core`, `config`, `redis`가 맡는다.
- public 타입은 가능한 한 작은 계약으로 유지한다.

## Public Types

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimiter`
- `RateLimitPolicy`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`
- `RateLimitException`

## Type Notes

### `RateLimitKey`

- key type과 value를 함께 담는다.
- `asString()`은 저장소 키로 쓰기 좋게 `TYPE:value` 형식으로 반환한다.

### `RateLimitPlan`

- capacity와 refill rate를 담는다.
- `perSecond(...)`는 읽기 쉬운 팩토리 메서드다.

### `RateLimitDecision`

- 허용 여부를 담는다.
- 남은 토큰 수와 retry-after milliseconds를 담는다.

### `RateLimitPolicy`

- 하나의 검사 단위를 표현한다.
- key, plan, permits를 함께 묶는다.

### `RateLimitKeyResolver`

- 요청이나 컨텍스트에서 key를 만든다.
- HTTP뿐 아니라 다른 환경에도 재사용할 수 있다.

### `RateLimitPolicyResolver`

- 요청이나 컨텍스트에서 정책 목록을 만든다.
- 여러 개의 rate limit rule을 한 번에 적용하는 용도다.

### `RateLimitException`

- 예외 기반 adapter가 필요할 때만 쓰는 보조 타입이다.

## Example Flow

1. key resolver가 `RateLimitKey`를 만든다.
2. policy resolver가 `RateLimitPolicy` 목록을 만든다.
3. 실행 계층이 `RateLimiter.tryAcquire(...)`를 호출한다.
4. 결과를 `RateLimitDecision`으로 받는다.

## What Belongs Here

- 순수 데이터 타입
- 인터페이스
- 외부 공개 계약

## What Does Not Belong Here

- 알고리즘 구현
- Spring Boot 설정
- Redis 연동
- 서비스별 운영 정책

## Related Docs

- [Architecture](../architecture.md)
- [Examples](../examples.md)
