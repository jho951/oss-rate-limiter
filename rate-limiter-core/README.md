# rate-limiter-core

`rate-limiter-core`는 공개 rate limiting 계약에서 사용하는 순수 값 객체와 판정 모델을 담는 모듈입니다.

## 언제 쓰나

- 공통 모델만 필요할 때
- 정책 정의와 판정 결과를 애플리케이션 내부에서 직접 조합할 때
- SPI 없이 값 객체만 재사용하고 싶을 때

## 제공 타입

- `RateLimitKey`
- `RateLimitKeyType`
- `RateLimitPlan`
- `RateLimitDecision`
- `RateLimitPolicy`
- `RateLimitException`

## 포함하지 않는 것

- SPI 인터페이스
- Spring Boot 연동 코드
- Redis 또는 저장소 구현체

전체 문서 목록은 [docs/readme.md](../docs/readme.md)를 참고하세요.
