# Docs

이 디렉터리는 `rate-limiter`를 처음 쓰는 사람과 유지보수하는 사람이 필요한 문서를 모아둡니다.

## 먼저 읽기

### 시작할 때

1. [아키텍처](./architecture.md)
2. [모듈 가이드](./modules.md)
3. [SPI/extension 가이드](./extension-guide.md)

### 문제를 만났을 때

1. [트러블슈팅](./troubleshooting.md)

### 모듈과 테스트

1. [테스트/CI 가이드](./test-and-ci.md)


## 읽는 순서

- 공개 계약 기준은 `oss-contract` 저장소를 봅니다.
- 처음 보는 사람은 `아키텍처`와 `모듈 가이드`부터 읽습니다.
- SPI를 직접 구현하는 경우 `SPI/extension 가이드`를 먼저 봅니다.
- 테스트와 CI 흐름을 확인할 때는 `테스트/CI 가이드`를 봅니다.
- 에러 원인을 좁혀야 하면 `트러블슈팅`부터 봅니다.
