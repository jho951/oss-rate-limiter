# rate-limiter

[![Build](https://github.com/jho951/ratelimiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ratelimiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-api)](https://central.sonatype.com/namespace/io.github.jho951)
[![License](https://img.shields.io/github/license/jho951/ratelimiter)](./LICENSE)

IP / 사용자 ID / API 키 기준으로 요청 빈도를 제한하는 재사용 가능한 Public OSS 모듈입니다.

## Overview

이 저장소의 1계층 책임은 `요청 주체 식별 -> 토큰 버킷 판정 -> 결과 반환`까지입니다.

## Quick Start

```gradle
dependencies {
  implementation("io.github.jho951:rate-limiter-config:1.1.0")
}
```

```java
TokenBucketRateLimiter limiter = new TokenBucketRateLimiter();
RateLimitDecision decision = limiter.tryAcquire(
    RateLimitKey.of(RateLimitKeyType.IP, "1.2.3.4"),
    1,
    new RateLimitPlan(10, 1)
);
```

Maven Central / Central Portal 배포용 POM 메타데이터와 서명 설정을 포함합니다.

## Documentation

- [Architecture](docs/architecture.md)
- [Examples](docs/examples.md)
- [Versioning Roadmap](docs/versioning-roadmap.md)
- [API Module](docs/modules/api.md)
- [Core Module](docs/modules/core.md)
- [Config Module](docs/modules/config.md)
- [Redis Module](docs/modules/redis.md)
