# 테스트/CI 가이드

## 로컬 테스트 실행

### 전체 빌드

```bash
./gradlew clean build
```

### 모듈 단위 테스트:

```bash
./gradlew :rate-limiter-core:test
./gradlew :rate-limiter-spi:test
```

## 현재 테스트 범위

- `rate-limiter-core`
  - `RateLimitContractTest`
  - `RateLimitValueObjectTest`
- `rate-limiter-spi`
  - `RateLimiterSpiContractTest`

## GitHub Actions 워크플로우

### 현재 워크플로우 파일

- `.github/workflows/_gradle.yml`
- `.github/workflows/build.yml`
- `.github/workflows/publish.yml`

### `_gradle.yml`

- 재사용 워크플로우입니다.
- 공통 수행:
  1. `actions/checkout`
  2. `actions/setup-java`
  3. `gradle/actions/setup-gradle`
  4. `./gradlew <task> --no-daemon --stacktrace`
- 필요 시 `release_version`을 Gradle project property로 주입합니다.


### `build.yml`

- 트리거:
    - `main` 대상 PR
    - `main` push
- 수행:
    - `./gradlew clean test --no-daemon --stacktrace`


### `publish.yml`

- 트리거: `v*` 태그 push
- 수행:
  1. 태그에서 `release_version` 계산
  2. `_gradle.yml`을 호출해 `test` 실행
  3. `_gradle.yml`을 호출해 `publishAggregationToCentralPortal` 실행
  4. `rate-limiter-core`와 `rate-limiter-spi`를 Maven Central에 배포

## 예시 검증 방식

- 사용 예시는 문서만 두지 않고 테스트 코드로도 함께 유지합니다.
- `rate-limiter-spi` 테스트는 최소 SPI 사용 흐름이 실제로 컴파일되고 동작하는지 검증합니다.

## 참고

CI와 문서는 소스 트리 기준으로 설명합니다.
generated build 산출물은 문서 기준이 아닙니다.
