# rate-limiter-core

이 문서는 `rate-limiter-core`의 판정 엔진과 상태 저장 모델을 설명합니다.

## 역할

`rate-limiter-core`는 Token Bucket 판정 엔진입니다.

- key와 plan을 받아 허용 / 차단을 계산
- remaining token과 retry-after를 계산
- Spring, HTTP, Redis에 의존하지 않는 알고리즘 본체

## 주요 타입

- `TokenBucketRateLimiter`
- `InMemoryTokenBucketStore`
- `TokenBucketStore`
- `TokenBucketState`
- `Clock`

## 실행 모델

- `TokenBucketRateLimiter`가 진입점입니다.
- `TokenBucketStore`가 상태 저장소 계약입니다.
- `InMemoryTokenBucketStore`가 기본 구현체입니다.
- `Clock`은 테스트 가능성을 위한 시간 추상화입니다.

## 동작 규칙

- key마다 독립적인 버킷을 유지합니다.
- 요청 수가 capacity를 넘으면 deny합니다.
- refill rate에 따라 시간이 지나면 토큰이 다시 채워집니다.
- `permits <= 0`은 1로 정규화합니다.
- `retryAfterMillis`는 재시도 가능 시점을 계산하기 위한 최소 힌트입니다.

## 예시

```java
import io.github.jho951.ratelimiter.api.RateLimitKey;
import io.github.jho951.ratelimiter.api.RateLimitKeyType;
import io.github.jho951.ratelimiter.api.RateLimitPlan;
import io.github.jho951.ratelimiter.core.TokenBucketRateLimiter;

TokenBucketRateLimiter limiter = new TokenBucketRateLimiter();
limiter.tryAcquire(
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    1,
    RateLimitPlan.perSecond(10, 1.0)
);
```

## 하지 않는 것

- HTTP filter
- Spring Boot auto configuration
- Redis script
- 서비스별 path 정책

## 관련 문서

- [Architecture](../architecture.md)
- [Examples](../examples.md)
