# rate-limiter

`rate-limiter`는 Java 17 기반의 요청 제한 OSS 모듈입니다.
핵심 모델과 SPI 계약을 한 묶음으로 제공합니다.

[![Build](https://github.com/jho951/rate-limiter/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/rate-limiter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/rate-limiter-core?label=maven%20central)](https://central.sonatype.com/search?q=jho951)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/rate-limiter)](https://github.com/jho951/rate-limiter/tags)

## 공개 좌표

- `io.github.jho951:rate-limiter-core`
- `io.github.jho951:rate-limiter-spi`

## 무엇을 제공하나

- `rate-limiter-core`: key, plan, decision, policy 같은 공통 모델
- `rate-limiter-spi`: 외부 구현이 따를 SPI 인터페이스

## 빠른 시작

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jho951:rate-limiter-core:<version>")
    implementation("io.github.jho951:rate-limiter-spi:<version>")
}
```

## 문서

- [docs/README.md](docs/README.md)
