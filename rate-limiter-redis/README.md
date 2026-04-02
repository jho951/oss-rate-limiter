# rate-limiter-redis

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Publish](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-redis?label=maven%20central)](https://central.sonatype.com/search?q=io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/rate-limiter)](../LICENSE)

이 모듈은 Redis 기반 분산 저장 구현입니다.

- 여러 인스턴스가 같은 버킷 상태를 공유
- `mode: redis`일 때만 활성화
- Token Bucket 판정은 core와 동일하고 저장소만 Redis로 변경

상세 설명은 [docs/modules/redis.md](../docs/modules/redis.md)를 참고하세요.

## 30초 요약

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-redis:1.0.0")
}
```

```yml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter:
```

## 포함 타입

- `RedisTokenBucketRateLimiter`
- `RedisRateLimiterAutoConfiguration`

## 역할

- Redis를 상태 저장소로 사용합니다.
- Lua script로 읽기 / 계산 / 쓰기를 원자적으로 처리합니다.
- 여러 인스턴스가 동일한 버킷 상태를 공유할 수 있게 합니다.
- `ratelimiter.mode=redis`일 때만 자동 구성됩니다.

## 런타임 동작

- `RedisTokenBucketRateLimiter`가 `StringRedisTemplate`를 통해 상태를 읽고 씁니다.
- `RateLimiterProperties.redis.key-prefix`로 Redis 키 접두사를 제어합니다.
- 각 key별 TTL을 계산해 상태를 오래 남기지 않습니다.

## 하지 않는 것

- 서비스별 정책 하드코딩
- 인증 / 권한 엔진
- 운영 콘솔
- 과금 정책

## 관련 문서

- [Architecture](../docs/architecture.md)
- [Redis Docs](../docs/modules/redis.md)
