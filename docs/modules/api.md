# rate-limiter-api

이 문서는 `rate-limiter-api`의 공개 계약과 경계를 설명합니다.

## 역할

`rate-limiter-api`는 외부에 노출되는 최소 계약만 담는 레이어입니다.

- rate limit 대상과 정책을 표현
- 판정 결과를 표현
- key resolver와 policy resolver의 계약을 제공
- Spring, Redis, Servlet, 저장소 구현과 분리

## 공개 타입

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimitPolicy`
- `RateLimiter`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`
- `RateLimitException`

## 타입 요약

### `RateLimitKey`

- key type과 value를 함께 담습니다.
- `asString()`은 저장소 키로 쓰기 좋은 `TYPE:value` 형식입니다.

### `RateLimitPlan`

- capacity와 refill rate를 담습니다.
- `perSecond(...)`는 읽기 쉬운 팩토리 메서드입니다.

### `RateLimitDecision`

- 허용 여부를 담습니다.
- 남은 토큰 수와 `retryAfterMillis`를 담습니다.

### `RateLimitPolicy`

- 하나의 rate limit 검사 단위를 표현합니다.
- key, plan, permits를 하나로 묶습니다.

### `RateLimitKeyResolver`

- 요청이나 컨텍스트에서 `RateLimitKey`를 만듭니다.
- HTTP뿐 아니라 다른 환경에도 재사용할 수 있습니다.

### `RateLimitPolicyResolver`

- 요청이나 컨텍스트에서 정책 목록을 만듭니다.
- 여러 개의 rule을 한 번에 적용할 때 씁니다.

### `RateLimitException`

- 예외 기반 adapter가 필요할 때만 쓰는 보조 타입입니다.

## 설계 원칙

- 순수 데이터와 인터페이스만 둡니다.
- 구현 전략은 `rate-limiter-core`, `rate-limiter-config`, `rate-limiter-redis`가 맡습니다.
- public 타입은 가능한 한 작고 명확하게 유지합니다.

## 동작 흐름

1. key resolver가 `RateLimitKey`를 만듭니다.
2. policy resolver가 `RateLimitPolicy` 목록을 만듭니다.
3. 실행 계층이 `RateLimiter.tryAcquire(...)`를 호출합니다.
4. 결과를 `RateLimitDecision`으로 받습니다.

## 하지 않는 것

- 알고리즘 구현
- Spring Boot 설정
- Redis 연동
- 서비스별 운영 정책

## 관련 문서

- [Architecture](../architecture.md)
- [Examples](../examples.md)
