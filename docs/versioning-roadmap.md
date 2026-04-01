# RateLimiter Versioning Roadmap

이 문서는 현재 `ratelimiter` 모듈을 어떤 기준으로 버전화하고, 각 버전에서 무엇을 유지하고 무엇을 확장할지 정의한다.

핵심 원칙은 다음과 같다.

- 버전 경계는 알고리즘 자체보다 `상태 저장 방식`과 `운영 모델 변화`를 기준으로 잡는다.
- `v1`은 단일 인스턴스용 모듈로 명확히 규정한다.
- 분산 환경 대응은 `v2`의 책임으로 분리한다.
- 운영 편의성, 관측성, 동적 정책 등은 `v2.x`에서 강화한다.

## 1. Version Strategy Summary

### v1
- 현재 구현 유지
- In-Memory Token Bucket
- 단일 JVM / 단일 Gateway 기준
- 빠른 도입과 단순성이 목표

### v1.1
- 저장소 추상화 도입
- 기존 기능과 동작은 유지
- `v2 Redis` 확장을 위한 구조 정리 단계

### v2
- Redis 기반 분산 Rate Limit 지원
- 여러 대의 Gateway에서 전역 한도 보장
- 실서비스 분산 환경 대응 시작점

### v2.x
- 운영 기능 강화
- 관측성, 정책 세분화, 장애 대응, 관리 기능 추가

## 2. v1: Current Baseline

## 목적

`v1`은 "단일 인스턴스에서 간단하고 빠르게 붙일 수 있는 RateLimiter"를 목표로 한다.

## 현재 기능

- Token Bucket 알고리즘 사용
- 키 단위 제한 대상 지원
  - IP
  - USER_ID
  - API_KEY
- Spring Boot Filter 자동 적용
- `429 Too Many Requests` 반환
- `Retry-After` 헤더 반환
- Pure Java에서도 직접 사용 가능

## 현재 기술적 특징

- 각 Rate Limit 키마다 버킷 상태를 메모리에 저장
- 버킷 동기화는 JVM 내부 `synchronized(state)` 기반
- 상태 저장소는 각 애플리케이션 인스턴스마다 독립적임

## 장점

- 구조가 단순함
- 외부 인프라 의존성이 없음
- 로컬 개발과 단일 서버 환경에서 빠르게 사용 가능

## 제한 사항

- 여러 대의 Gateway 환경에서 전역 Rate Limit 보장 불가
- 인스턴스 수만큼 사실상 허용량이 증가
- 서버 재시작 시 버킷 상태 유실
- 운영 정책 변경, 메트릭, 중앙 제어 기능이 약함

## v1을 유지해야 하는 이유

- 외부 인프라가 없는 환경에서는 여전히 유효함
- 테스트/개발/PoC 단계에서 가장 단순한 기본값 역할을 함
- 이후 버전에서도 `memory profile`로 남겨둘 가치가 있음

## v1에서 문서로 명확히 고정할 내용

- `v1은 단일 인스턴스용`이라는 점을 README와 JavaDoc에 분명히 표기
- 다중 Gateway에서는 제한이 인스턴스별로 분리된다는 점 명시
- 분산 환경 사용자는 `v2 Redis`를 사용하도록 유도

## 3. v1.1: Store Abstraction

## 목적

`v1.1`은 기능 추가 버전이 아니라 `구조 정리 버전`이다. 사용자는 기존처럼 쓰되, 내부 설계를 분산 확장 가능하게 바꾼다.

즉, 이 단계의 목표는 다음 한 문장으로 요약된다.

`"현재 동작은 유지하면서 저장소 구현을 교체 가능한 구조로 만든다."`

## 왜 필요한가

현재 구조는 `TokenBucketRateLimiter`가 `InMemoryTokenBucketStore`에 직접 결합되어 있다. 이 상태에서는 Redis 저장소를 붙이더라도 구현이 지저분해지고, AutoConfiguration도 버전별로 분기하기 어려워진다.

`v1.1`에서 이 결합을 끊어야 `v2`를 자연스럽게 만들 수 있다.

## 목표 범위

- 외부 API의 큰 변경 없이 내부 저장소 추상화
- 메모리 저장소는 기본 구현체로 유지
- 추후 Redis 구현체를 붙일 수 있는 확장 포인트 확보

## 권장 구조 변경

### 1. Store 인터페이스 도입

예시 책임:

- 키별 버킷 상태 조회
- 초기 상태 생성
- 상태 갱신
- 유휴 버킷 정리

이 시점에는 인터페이스 이름을 다음 중 하나로 정하면 된다.

- `TokenBucketStore`
- `RateLimitStateStore`

권장안은 `TokenBucketStore`다. 현재 알고리즘이 Token Bucket에 고정되어 있기 때문이다.

### 2. 구현체 분리

- `InMemoryTokenBucketStore`는 인터페이스 구현체로 변경
- `TokenBucketRateLimiter`는 구체 클래스 대신 인터페이스에 의존

### 3. AutoConfiguration 정리

- 기본 Bean은 `InMemoryTokenBucketStore`
- `TokenBucketRateLimiter`는 Store Bean을 주입받아 생성
- 이후 `v2`에서 Redis Bean이 있으면 교체 가능하도록 준비

### 4. 패키지 분리 검토

현재는 `core` 모듈 안에 memory store가 함께 있다. `v1.1`에서는 최소 변경으로 유지해도 되지만, `v2`를 생각하면 아래 구조가 더 낫다.

- `ratelimiter-api`
- `ratelimiter-core`
- `ratelimiter-memory`
- `ratelimiter-config`

다만 이 단계에서 모듈 분리까지 하면 작업 범위가 커질 수 있으므로, 다음 두 옵션 중 하나를 선택한다.

옵션 A
- `core` 안에서 인터페이스 + memory 구현체만 분리
- 가장 현실적인 `v1.1`

옵션 B
- `memory` 모듈까지 분리
- 장기적으로 더 깔끔하지만 범위가 커짐

현 시점에서는 `옵션 A`가 적절하다.

## v1.1에서 같이 정리해야 할 것

### 설정 검증

- `capacity > 0`
- `refillPerSecond > 0`
- 잘못된 설정일 때 애플리케이션 시작 실패

### 키 추출 정책 명확화

- `X-Forwarded-For` 사용 조건 명시
- 프록시 신뢰 범위 문서화
- 헤더 위조 가능성에 대한 주의사항 추가

### 테스트 강화

- 단일 키에서 정상 차감 테스트
- refill 계산 테스트
- 동시성 테스트
- 설정 검증 실패 테스트
- Filter 레벨 통합 테스트

## v1.1 완료 기준

- 외부 사용 방식이 거의 변하지 않음
- 기본 동작은 여전히 In-Memory
- `TokenBucketRateLimiter`가 Store 인터페이스에만 의존
- 향후 Redis 구현을 위한 코드 재작업이 크지 않음

## 4. v2: Redis Distributed Rate Limiting

## 목적

`v2`는 여러 대의 Gateway에서 동일한 정책을 공유하는 `분산 Rate Limiting`을 제공한다.

이 버전부터는 "실서비스 분산 운영 가능"이 핵심 가치다.

## 해결해야 하는 문제

`v1`은 Gateway마다 메모리가 분리되어 있기 때문에, 같은 사용자라도 Gateway A와 Gateway B에서 각각 별도 한도를 갖는다.

`v2`는 다음을 만족해야 한다.

- 어느 Gateway로 요청이 들어와도 동일한 버킷 상태를 사용
- 동시에 여러 요청이 들어와도 한도 계산이 깨지지 않음
- 버킷 상태가 원자적으로 갱신됨

## 저장소 선택

분산 저장소는 Redis를 기본 권장안으로 한다.

이유:

- 단일 key 읽기/쓰기 속도가 빠름
- TTL 관리가 쉬움
- Lua script를 통한 원자 연산 지원
- Rate Limiting 용도로 검증된 패턴이 많음

## 핵심 설계 원칙

### 1. 원자성 보장

Redis에서 아래 순서를 애플리케이션 코드로 나눠 처리하면 안 된다.

- 상태 읽기
- refill 계산
- 토큰 차감
- 상태 저장

이 방식은 경쟁 조건이 생긴다.

반드시 다음 중 하나를 사용한다.

- Lua script
- Redis 단일 atomic operation 조합

권장안은 `Lua script`다.

### 2. 저장 데이터 표준화

키 예시:

- `ratelimiter:ip:1.2.3.4`
- `ratelimiter:user:12345`
- `ratelimiter:api-key:abcd1234`

저장 상태 예시:

- `tokens`
- `lastRefillAtMillis`

### 3. TTL 적용

오랫동안 쓰이지 않은 버킷은 자동 삭제되어야 한다.

TTL은 최소 다음 기준 중 하나로 계산한다.

- `capacity / refillRate` 기반 복구 시간
- 운영자가 명시적으로 지정한 idle TTL

권장 초기안:

- 버킷이 가득 찬 뒤 일정 시간 미사용이면 만료

### 4. 장애 정책 정의

Redis 장애 시 정책을 선택할 수 있어야 한다.

- `fail-open`
  - Redis 오류 시 요청을 허용
  - 가용성 우선
- `fail-close`
  - Redis 오류 시 요청을 차단
  - 보호 우선

기본값은 일반적으로 `fail-open`이 현실적이다. 다만 보안성 높은 API는 `fail-close`가 필요할 수 있다.

## v2에서 추가될 설정 예시

```yaml
ratelimiter:
  mode: redis
  redis:
    key-prefix: ratelimiter
    timeout-millis: 200
    fail-open: true
    idle-ttl-seconds: 300
```

## 권장 모듈 구조

`v2`부터는 다음 구조를 권장한다.

- `ratelimiter-api`
- `ratelimiter-core`
- `ratelimiter-memory`
- `ratelimiter-redis`
- `ratelimiter-config`

모듈별 책임은 다음과 같다.

- `api`
  - 공개 타입
- `core`
  - 공통 알고리즘
  - store 인터페이스
  - 공통 모델
- `memory`
  - In-Memory 구현
- `redis`
  - Redis 구현
  - Lua script
  - Redis 관련 설정
- `config`
  - Spring Boot AutoConfiguration
  - 조건부 Bean 등록

## v2에서 검증해야 할 테스트

- 단일 key 분산 환경 테스트
- 다중 Gateway 동시성 테스트
- Lua script 원자성 테스트
- Redis 재시작/Timeout 시 장애 정책 테스트
- TTL 만료 테스트
- 성능 테스트

## v2 완료 기준

- 여러 대의 Gateway에서 동일한 전역 한도 보장
- Redis 사용 시 원자적 토큰 차감 보장
- 장애 정책이 문서화되고 설정 가능
- 운영 환경에서 적용 가능한 수준의 테스트 확보

## 5. v2.x: Operations and Productization

## 목적

`v2.x`는 분산 기능 자체보다 `운영 완성도`를 높이는 단계다.

즉, "Redis로 된다"에서 끝나지 않고, 실제 운영자가 관리하고 관찰하고 조정할 수 있게 만든다.

## 강화 영역

### 1. 관측성

- 허용/차단 카운터 메트릭
- key type별 메트릭
- endpoint별 메트릭
- Redis 에러 메트릭
- 평균 retry-after 시간 메트릭

Micrometer 연동을 권장한다.

### 2. 응답 헤더 표준화

선택적으로 다음 헤더 지원:

- `X-RateLimit-Limit`
- `X-RateLimit-Remaining`
- `X-RateLimit-Reset`
- `Retry-After`

### 3. 정책 세분화

- endpoint별 정책
- HTTP method별 정책
- 사용자 등급별 정책
- API key 등급별 정책
- 공통 기본 정책 + override 정책

### 4. 예외 처리와 운영 편의성

- 특정 health check path 제외
- 특정 내부 CIDR 제외
- 관리자 화이트리스트
- 내부 서비스 계정 예외 처리

### 5. 동적 정책 변경

초기에는 정적 설정으로 시작하되, 필요 시 다음으로 확장한다.

- DB 기반 정책 로딩
- Config Server 기반 정책 관리
- 관리 API를 통한 정책 갱신

### 6. 운영 안전성

- Redis timeout 세분화
- circuit breaker 연계
- fallback 정책 고도화
- 로그 샘플링
- 과도한 key cardinality 방지

## v2.x 세부 마일스톤 예시

### v2.1

- Micrometer 메트릭 추가
- 표준 Rate Limit 헤더 추가
- health/actuator path 제외 옵션 추가

### v2.2

- endpoint별 정책 지원
- whitelist / blacklist 추가
- key prefix 및 namespace 운영 옵션 강화

### v2.3

- 동적 정책 변경 지원
- 운영 관리 API 또는 외부 정책 소스 연동

### v2.4

- circuit breaker / fallback 개선
- 대규모 트래픽 환경에서 성능 최적화

## v2.x 완료 기준

- 운영자가 지표로 상태를 볼 수 있음
- 정책을 서비스 특성에 맞게 세분화 가능
- 장애 상황에서 의도된 방식으로 동작
- 확장 기능이 추가되어도 코어 계약이 크게 흔들리지 않음

## 6. Compatibility Policy

버전별 호환성 정책은 다음 기준을 권장한다.

### v1 -> v1.1

- 가능한 한 하위 호환 유지
- 기존 사용자는 설정 변경 없이 계속 사용 가능해야 함
- 내부 구조 변경이 주가 되어야 함

### v1.1 -> v2

- 기능 확장 성격이지만 운영 모델이 바뀌므로 `minor`보다 `major` 성격에 가깝게 관리 가능
- Redis 모드가 추가되더라도 memory 모드는 유지 가능
- 다만 모듈 분리나 자동 설정 방식이 크게 바뀌면 문서와 마이그레이션 가이드 필요

### v2 -> v2.x

- 가능하면 하위 호환 유지
- 새 기능은 opt-in으로 추가
- 기본 정책을 갑자기 바꾸지 않음

## 7. Recommended Release Notes Format

각 릴리스 문서는 다음 항목을 포함하는 것을 권장한다.

- 목적
- 주요 변경 사항
- 하위 호환 여부
- 설정 변경 사항
- 마이그레이션 필요 여부
- 테스트 범위
- 운영상 주의 사항

## 8. Recommended Next Steps

현재 코드베이스 기준으로 가장 현실적인 다음 단계는 다음과 같다.

### Step 1

`v1.1` 작업 수행

- `TokenBucketStore` 인터페이스 도입
- `TokenBucketRateLimiter` 의존성 역전
- 설정 검증 추가
- 테스트 보강

### Step 2

`v2`용 모듈 분리 시작

- `ratelimiter-memory`
- `ratelimiter-redis`

### Step 3

Redis Lua script 기반 분산 토큰 차감 구현

### Step 4

운영 기능은 `v2.1`부터 단계적으로 추가

## 9. Final Recommendation

이 모듈은 다음 포지션으로 가져가는 것이 가장 명확하다.

- `v1`: 단일 서버용 경량 RateLimiter
- `v1.1`: 확장을 위한 구조 정리
- `v2`: Redis 기반 분산 RateLimiter
- `v2.x`: 운영형 제품으로 고도화

가장 중요한 원칙은 한 가지다.

`분산 환경 지원은 단순 기능 추가가 아니라 저장소와 운영 모델이 바뀌는 단계이므로, v2로 명확히 분리해서 관리한다.`
