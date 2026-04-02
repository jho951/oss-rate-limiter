# Architecture

이 문서는 `rate-limiter`의 계층 구조와 요청 흐름을 설명합니다.

## 개요

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

## 계층

### `rate-limiter-api`

외부에 노출되는 공개 계약만 둡니다.

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitPolicy`
- `RateLimitDecision`
- `RateLimiter`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`

### `rate-limiter-core`

판정 엔진만 둡니다.

- Token Bucket 알고리즘
- in-memory store
- `tryAcquire(...)`
- remaining token 계산
- retry-after 계산

### `rate-limiter-config`

Spring Boot adapter만 둡니다.

- AutoConfiguration
- Filter
- Properties
- HTTP 요청에서 key 추출
- 기본 policy 생성
- `excluded-paths`
- `trust-forward-headers`
- `fail-open`

### `rate-limiter-redis`

선택적 분산 저장 구현만 둡니다.

- Redis 기반 distributed rate limiter
- 알고리즘 자체가 아니라 backend/store 구현
- `mode: redis`일 때만 사용

## 요청 흐름

1. HTTP 요청이 `rate-limiter-config`로 들어옵니다.
2. `RateLimitKeyResolver`가 `RateLimitKey`를 만듭니다.
3. `RateLimitPolicyResolver`가 `RateLimitPolicy` 목록을 만듭니다.
4. `rate-limiter-core` 또는 `rate-limiter-redis`가 `tryAcquire(...)`를 수행합니다.
5. 결과를 `RateLimitDecision`으로 돌려줍니다.
6. 차단 시 `429`와 `Retry-After`를 응답합니다.

## Redis가 선택인 이유

Redis는 1계층의 본질이 아니라 분산 저장 방식입니다.

- 단일 인스턴스는 `core + in-memory`만으로 충분합니다.
- 다중 Gateway에서는 동일한 버킷 상태를 공유해야 하므로 Redis가 필요할 수 있습니다.
- 따라서 Redis는 OSS 안에 포함되지만 핵심 책임으로 고정하지 않습니다.

## 하지 않는 것

- 서비스별 path 정책 플랫폼
- 권한 엔진
- 인증 서버 결합 모듈
- 과금 / 상품 플랜 정책 엔진
- 운영 콘솔
- 멀티테넌트 정책 허브

## 모듈 문서

- [API](modules/api.md)
- [Core](modules/core.md)
- [Config](modules/config.md)
- [Redis](modules/redis.md)
