# rate-limiter-redis

이 문서는 `rate-limiter-redis`의 분산 저장 구현을 설명합니다.

## 역할

`rate-limiter-redis`는 Redis를 상태 저장소로 쓰는 선택 확장입니다.

- 여러 인스턴스가 같은 버킷 상태를 공유
- `mode: redis`일 때만 활성화
- core와 같은 Token Bucket 규칙을 따르되 저장소만 Redis로 변경

## 주요 타입

- `RedisTokenBucketRateLimiter`
- `RedisRateLimiterAutoConfiguration`

## 런타임 동작

- `RedisTokenBucketRateLimiter`가 `StringRedisTemplate`를 통해 상태를 읽고 씁니다.
- Lua script로 읽기 / 계산 / 쓰기를 원자적으로 처리합니다.
- `RateLimiterProperties.redis.keyPrefix`로 Redis 키 접두사를 제어합니다.
- `ratelimiter.mode=redis`일 때만 auto configuration이 활성화됩니다.

## 예시

```yaml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter:
```

```java
// Redis implementation is activated by Spring Boot auto-configuration.
```

## 하지 않는 것

- 서비스별 정책 하드코딩
- 인증 엔진
- 운영 콘솔
- 과금 정책

## 관련 문서

- [Architecture](../architecture.md)
- [Examples](../examples.md)
