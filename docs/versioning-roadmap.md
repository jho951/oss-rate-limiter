# RateLimiter Versioning Roadmap

이 문서는 `rate-limiter` 모듈의 1계층 책임과 버전별 확장 방향을 정리한다.

## 1계층 원칙

- OSS의 핵심 책임은 `요청 주체 식별 -> 토큰 버킷 판정 -> 결과 반환`이다.
- `core`는 알고리즘과 in-memory store를 담당한다.
- `config`는 Spring Boot adapter 이다.
- `redis`는 선택적 분산 확장 모듈이다.
- 서비스별 path 정책, 권한 판단, 과금 정책, 운영 콘솔은 2계층 플랫폼 책임이다.

## Current Baseline

현재 구현은 다음을 기준으로 한다.

- Token Bucket 알고리즘
- 키 단위 제한 대상
- Spring Boot Filter 자동 적용
- `429 Too Many Requests` 반환
- `Retry-After` 헤더 반환
- Pure Java에서도 직접 사용 가능

## v1

목표는 단일 인스턴스에서 빠르게 붙일 수 있는 기본 rate limiter 제공이다.

- In-Memory Token Bucket
- 단일 JVM / 단일 Gateway 기준
- 외부 인프라 의존성 없음

## v1.1

목표는 동작을 크게 바꾸지 않고 구조를 정리하는 것이다.

- 저장소 추상화 도입
- 메모리 저장소는 기본 구현체로 유지
- Redis 구현을 붙일 수 있는 확장 포인트 확보

## v2

목표는 여러 대의 Gateway에서 동일한 정책을 공유하는 분산 rate limiting 이다.

- Redis 기반 분산 Rate Limit 지원
- 여러 Gateway에서 전역 한도 보장
- 원자적 상태 갱신

## v2.x

목표는 운영과 확장 기능을 강화하는 것이다.

- 관측성
- 정책 세분화
- 장애 대응
- 관리 기능

## 문서에서 고정할 것

- `v1은 단일 인스턴스용`이라는 점을 명시한다.
- 다중 Gateway에서는 제한이 인스턴스별로 분리된다는 점을 명시한다.
- 분산 환경 사용자는 `v2 Redis`를 사용하도록 유도한다.
