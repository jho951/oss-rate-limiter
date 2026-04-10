# rate-limiter-core

`rate-limiter-core`는 rate limiter의 공통 모델을 담는 1계층 모듈입니다.

## 제공 타입

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimitPolicy`
- `RateLimitException`

## 무엇을 하지 않나

- SPI 인터페이스를 두지 않습니다.
- Spring Boot 코드를 두지 않습니다.
- Redis 구현을 두지 않습니다.

상세 설명은 [docs/README.md](../docs/README.md)를 참고하세요.
