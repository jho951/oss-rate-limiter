# Modules

현재 공개 모듈은 `core`, `spi`, `redis-jedis` 세 개입니다.

## 모듈 요약

| Module | Responsibility | Artifact |
| --- | --- | --- |
| `rate-limiter-core` | 핵심 모델과 값 객체 | `io.github.jho951:rate-limiter-core` |
| `rate-limiter-spi` | SPI 인터페이스 | `io.github.jho951:rate-limiter-spi` |
| `rate-limiter-redis-jedis` | Jedis 기반 Redis 구현 | `io.github.jho951:rate-limiter-redis-jedis` |

## 의존 관계

- `rate-limiter-spi` -> `rate-limiter-core`
- `rate-limiter-redis-jedis` -> `rate-limiter-spi` -> `rate-limiter-core`
- `rate-limiter-redis-jedis` -> Jedis 2.9.3

## 소비 가이드

- 대부분의 사용자는 `rate-limiter-spi`만 의존하면 됩니다.
- 값 객체만 필요하면 `rate-limiter-core`만 사용합니다.
- `rate-limiter-spi`를 의존하면 `rate-limiter-core` 타입도 함께 사용할 수 있습니다.
- Redis를 사용할 때만 `rate-limiter-redis-jedis`를 추가합니다. 이 모듈은 Spring에 의존하지 않습니다.

## 공개 API 원칙

- `rate-limiter-core`는 순수 모델만 유지합니다.
- `rate-limiter-spi`는 외부 구현이 따라야 하는 인터페이스만 유지합니다.
- Redis 연결 풀과 Spring Bean 등록은 소비 애플리케이션이 구성합니다.
- 입력 모델과 판정 의미는 `core`와 `spi`에서만 확장합니다.
