# rate-limiter-spi

`rate-limiter-spi`는 rate limiting을 각자의 런타임이나 프레임워크에 연결할 때 사용하는 확장 계약을 담는 모듈입니다.

이 모듈은 `rate-limiter-core`에 의존하며, 대부분의 사용자에게 권장되는 진입점입니다.

## 언제 쓰나

- 애플리케이션에서 rate limiting 구현체를 직접 주입할 때
- key 생성, policy 해석, limiter 실행 계약을 분리하고 싶을 때
- 프레임워크별 adapter를 별도 계층에서 만들 계획일 때

## 제공 타입

- `RateLimiter`
- `RateLimitKeyResolver`
- `RateLimitPolicyResolver`

## 포함하지 않는 것

- core 모델 정의 책임
- Spring Boot 연동 코드
- Redis 또는 저장소 구현체

전체 문서 목록은 [docs/readme.md](../docs/readme.md)를 참고하세요.
