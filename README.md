# rate-limiter

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-spi?label=maven%20central)](https://central.sonatype.com/search?q=g%3Aio.github.jho951%20AND%20a%3Arate-limiter-spi)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/rate-limiter)](https://github.com/jho951/rate-limiter/tags)

## 제공 모듈

- `rate-limiter-spi` : SPI 계약을 제공하고 `rate-limiter-core`를 전이 의존성으로 가져옵니다.
- `rate-limiter-core` : 값 객체와 판정 모델만 필요할 때 사용합니다.
- `rate-limiter-redis-jedis` : Jedis 2.9.3으로 Redis 토큰 버킷을 원자 처리합니다.

## 요구 사항

- Java 8+
- 로컬 빌드와 테스트는 Gradle Wrapper(`./gradlew`) 기준으로 설명합니다.

## 빠른 시작

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jho951:rate-limiter-spi:<version>")
}
```

공통 모델만 필요하면 `rate-limiter-core`를 사용합니다.

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-core:<version>")
}
```

Redis 구현이 필요하면 다음 모듈을 사용합니다. Redis 연결 풀은 애플리케이션에서 구성하고 `JedisRateLimiter`에 전달합니다.

```gradle
dependencies {
    implementation("io.github.jho951:rate-limiter-redis-jedis:<version>")
}
```

로컬 Redis 통합 테스트는 `RATE_LIMIT_REDIS_TEST_HOST`를 설정해 실행합니다. 설정하지 않으면 Redis 통합 테스트는 건너뜁니다.

```powershell
$env:RATE_LIMIT_REDIS_TEST_HOST = "127.0.0.1"
$env:RATE_LIMIT_REDIS_TEST_PORT = "6379"
./gradlew :rate-limiter-redis-jedis:test
```

## 문서

- [docs/readme.md](./docs/readme.md)
