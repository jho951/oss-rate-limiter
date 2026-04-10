# Modules

## 모듈 목록

| Module | Responsibility | Artifact |
| --- | --- | --- |
| `rate-limiter-core` | 핵심 모델과 값 객체 | `io.github.jho951:rate-limiter-core` |
| `rate-limiter-spi` | SPI 인터페이스 | `io.github.jho951:rate-limiter-spi` |

## 의존 관계

- `rate-limiter-spi` -> `rate-limiter-core`

## 공개 API 원칙

- `rate-limiter-core`는 순수 모델만 유지합니다.
- `rate-limiter-spi`는 외부 구현이 따라야 하는 인터페이스만 유지합니다.
- 입력 모델과 판정 의미는 `core`와 `spi`에서만 확장합니다.

