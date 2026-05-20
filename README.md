# rate-limiter

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-spi?label=maven%20central)](https://central.sonatype.com/search?q=g%3Aio.github.jho951%20AND%20a%3Arate-limiter-spi)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/rate-limiter)](https://github.com/jho951/rate-limiter/tags)

## 제공 모듈

- `rate-limiter-spi` : SPI 계약을 제공하고 `rate-limiter-core`를 전이 의존성으로 가져옵니다.
- `rate-limiter-core` : 값 객체와 판정 모델만 필요할 때 사용합니다.

## 요구 사항

- Java 17+
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

## 문서

- [docs/readme.md](./docs/readme.md)
