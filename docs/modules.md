# Modules

현재 공개 모듈은 `core`와 `spi` 두 개입니다.

## 모듈 요약

| Module | Responsibility | Artifact |
| --- | --- | --- |
| `rate-limiter-core` | 핵심 모델과 값 객체 | `io.github.jho951:rate-limiter-core` |
| `rate-limiter-spi` | SPI 인터페이스 | `io.github.jho951:rate-limiter-spi` |

## 의존 관계

- `rate-limiter-spi` -> `rate-limiter-core`

## 소비 가이드

- 대부분의 사용자는 `rate-limiter-spi`만 의존하면 됩니다.
- 값 객체만 필요하면 `rate-limiter-core`만 사용합니다.
- `rate-limiter-spi`를 의존하면 `rate-limiter-core` 타입도 함께 사용할 수 있습니다.

## 공개 API 원칙

- `rate-limiter-core`는 순수 모델만 유지합니다.
- `rate-limiter-spi`는 외부 구현이 따라야 하는 인터페이스만 유지합니다.
- 입력 모델과 판정 의미는 `core`와 `spi`에서만 확장합니다.
