# rate-limiter-redis

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-redis)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](../LICENSE)

Redis 기반 분산 구현 모듈이다.

상세 설명은 [docs/modules/redis.md](../docs/modules/redis.md)를 본다.

## Contains

- `RedisTokenBucketRateLimiter`
- `RedisRateLimiterAutoConfiguration`

## Role

- 여러 인스턴스가 같은 버킷 상태를 공유하도록 한다.
- `mode: redis`일 때 선택적으로 활성화한다.
- 알고리즘은 `core`와 같은 Token Bucket 규칙을 따르되 저장소를 Redis로 둔다.

## Do Not Put Here

- 서비스별 정책 하드코딩
- 인증 / 권한 엔진
- 운영 콘솔
- 과금 정책
