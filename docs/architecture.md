# Architecture

이 문서는 `rate-limiter`의 1계층 구조를 설명한다.

## Overview

```text
HTTP Request
     |
     v
rate-limiter-config
     |
     +--> RateLimitKeyResolver
     |
     +--> RateLimitPolicyResolver
     |
     v
rate-limiter-core  <---->  rate-limiter-redis
     |
     v
RateLimitDecision
```

## Layering

### 1. `rate-limiter-api`

외부에 노출되는 공개 계약만 둔다.

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimiter`
- 교체 가능한 최소 인터페이스

### 2. `rate-limiter-core`

판정 엔진만 둔다.

- Token Bucket 알고리즘
- in-memory store
- `tryAcquire(...)`
- remaining token / retry-after 계산
- 순수 Java 사용 가능

### 3. `rate-limiter-config`

Spring Boot adapter 만 둔다.

- AutoConfiguration
- Filter
- Properties
- HTTP 요청에서 key 추출
- 기본 정책 적용
- `excluded-paths`
- `trust-forward-headers`
- `fail-open`

### 4. `rate-limiter-redis`

선택적 분산 저장 구현만 둔다.

- Redis 기반 distributed rate limiter
- 알고리즘 자체가 아니라 backend/store 구현
- `mode: redis`일 때만 사용

## What This Module Is Not

- 서비스별 path 정책 플랫폼
- 권한 엔진
- 인증 서버 결합 모듈
- 과금 / 상품 플랜 정책 엔진
- 운영 콘솔
- 멀티테넌트 정책 허브

## Request Flow

1. HTTP 요청이 `config`로 들어온다.
2. `RateLimitKeyResolver`가 `RateLimitKey`를 만든다.
3. `RateLimitPolicyResolver`가 `RateLimitPlan`을 결정한다.
4. `core` 또는 `redis` 구현이 `tryAcquire(...)`를 수행한다.
5. 결과를 `RateLimitDecision`으로 돌려준다.
6. 차단 시 `429`와 `Retry-After`를 응답한다.

## Why Redis Is Optional

Redis는 1계층의 본질이 아니라 분산 저장 방식이다.

- 단일 인스턴스는 `core + in-memory`만으로 충분하다.
- 다중 Gateway에서는 동일한 버킷 상태를 공유해야 하므로 Redis가 필요할 수 있다.
- 따라서 Redis는 OSS 안에 들어가도 되지만, 핵심 책임으로 고정하지는 않는다.

## Module Docs

- [API](modules/api.md)
- [Core](modules/core.md)
- [Config](modules/config.md)
- [Redis](modules/redis.md)
