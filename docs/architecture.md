# Architecture

## 계층

- `rate-limiter-core` : 핵심 모델과 값 객체를 제공합니다.
- `rate-limiter-spi` : 외부 구현이 따르는 SPI 인터페이스를 제공합니다.

## 경계 원칙

- 핵심 모델은 `rate-limiter-core`에만 둡니다.
- SPI 계약은 `rate-limiter-spi`에만 둡니다.
- Spring, starter, config, Redis 같은 구현은 이 저장소에 두지 않습니다.

## 동작 흐름

1. 요청 주체를 `RateLimitKey`로 표현합니다.
2. 제한 정책을 `RateLimitPlan`과 `RateLimitPolicy`로 표현합니다.
3. 실행 계층이 `RateLimiter`를 호출합니다.
4. 결과를 `RateLimitDecision`으로 받습니다.

## 1계층 OSS 기준

- 특정 서비스 도메인에 종속되지 않습니다.
- 인증이나 권한 엔진이 아니라 요청 제한 계약을 정의합니다.
- 구현 방식이 바뀌어도 모델 의미는 유지되어야 합니다.
- 프레임워크 연동 예시가 필요하면 별도 2계층 문서나 애플리케이션에서 설명합니다.
