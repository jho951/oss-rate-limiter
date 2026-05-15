# 트러블슈팅

`rate-limiter`의 공개 계약을 사용할 때 자주 만나는 문제와 확인 순서입니다.

## 1. `RateLimitKey`를 생성할 수 없다

### 원인
- `type`이 `null` 입니다.
- `value`가 `null` 이거나 blank 입니다.

### 조치
- `RateLimitKeyType`을 먼저 정하고, 식별자 문자열이 비어 있지 않은지 확인합니다.

## 2. `RateLimitPlan` 생성이 실패한다

### 원인
- `capacity`가 `0` 이하입니다.
- `refillTokensPerSecond`가 `0` 이하입니다.

### 조치
- capacity와 refill rate를 양수로 넣습니다.
- 운영 정책에서 0을 허용할 필요가 있으면 별도 정책 객체에서 정규화합니다.

## 3. `RateLimitPolicy`의 permits가 기대와 다르다

### 원인
- `permits <= 0` 이면 `IllegalArgumentException`이 발생합니다.

### 조치
- 호출 전 permits 값을 양수인지 명시적으로 확인합니다.
- 한 번의 검사 단위를 1토큰으로 볼지, 여러 토큰으로 볼지 정책을 분리합니다.

## 4. `RateLimitPolicy.of(...)` 호출 결과가 예상과 다르다

### 원인
- `name`, `key`, `plan` 중 하나가 `null` 입니다.
- `permits`가 `0` 이하입니다.

### 조치
- 정책 이름, key, plan을 모두 채운 뒤 생성합니다.
- null 허용이 필요한 경우 호출부에서 별도 조건 분기를 둡니다.

## 5. `RateLimitException`을 받아도 차단 이유가 불분명하다

### 원인
- `RateLimitException`은 결과 전달용 보조 타입입니다.
- 실제 차단 이유는 `RateLimitDecision` 안에 담겨 있습니다.

### 조치
- 예외 메시지보다 `RateLimitDecision`을 먼저 확인합니다.
- 예외 기반 흐름이 필요 없으면 `RateLimitDecision` 반환형을 직접 사용합니다.

## 6. SPI 구현을 찾을 수 없다

### 원인
- `rate-limiter-spi`는 인터페이스만 제공합니다.
- 실제 동작 구현은 애플리케이션이나 별도 2계층에서 주입해야 합니다.

### 조치
- `RateLimiter`, `RateLimitKeyResolver`, `RateLimitPolicyResolver` 구현체를 준비합니다.
- 프레임워크 연동이 필요하면 별도 adapter 계층에서 연결합니다.

## 7. `rate-limiter-spi`만 의존했는데 타입을 못 찾는다

### 원인
- `rate-limiter-spi`는 `rate-limiter-core`를 전이 의존성으로 노출합니다.
- 보통은 import 누락, 잘못된 버전, IDE 동기화 문제 때문에 타입이 보이지 않습니다.

### 조치
- 먼저 `io.github.jho951:rate-limiter-spi:<version>`만 의존하고 있는지 확인합니다.
- Gradle refresh 또는 IDE 프로젝트 동기화를 다시 실행합니다.
- import 경로가 `io.github.jho951.ratelimiter.core` / `spi`인지 확인합니다.

## 8. Gradle에서 `release_version`을 찾지 못한다

### 원인
- publish 경로는 `release_version`을 필수로 봅니다.
- 로컬 실행 시 `gradle.properties` 또는 `-Prelease_version`이 없을 수 있습니다.

### 조치
- 로컬에서는 `gradle.properties`의 값을 확인합니다.
- 릴리스 시에는 태그와 함께 `release_version`을 주입합니다.

## 9. Maven Central publish가 실패한다

### 원인
- `MAVEN_CENTRAL_USERNAME` 또는 `MAVEN_CENTRAL_PASSWORD`가 없습니다.
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY` 또는 signing password가 없습니다.

### 조치
- publish job에 필요한 secret을 모두 넣습니다.
- 배포 전에 `./gradlew test`가 먼저 통과하는지 확인합니다.

## 10. `rate-limiter-core` 테스트는 지나가는데 publish가 안 된다

### 원인
- 테스트와 publish는 별도 단계입니다.
- publish task는 signing과 Central Portal credentials를 추가로 요구합니다.

### 조치
- 테스트 통과만으로 배포 가능하다고 보지 않습니다.
- `build.gradle`의 publish 설정과 GitHub Actions secret을 같이 확인합니다.

## 11. 먼저 확인할 파일

- [RateLimitKey.java](/Users/jhons/Downloads/BE/oss/rate-limiter/rate-limiter-core/src/main/java/io/github/jho951/ratelimiter/core/RateLimitKey.java)
- [RateLimitPlan.java](/Users/jhons/Downloads/BE/oss/rate-limiter/rate-limiter-core/src/main/java/io/github/jho951/ratelimiter/core/RateLimitPlan.java)
- [RateLimitPolicy.java](/Users/jhons/Downloads/BE/oss/rate-limiter/rate-limiter-core/src/main/java/io/github/jho951/ratelimiter/core/RateLimitPolicy.java)
- [RateLimiter.java](/Users/jhons/Downloads/BE/oss/rate-limiter/rate-limiter-spi/src/main/java/io/github/jho951/ratelimiter/spi/RateLimiter.java)
- [build.gradle](/Users/jhons/Downloads/BE/oss/rate-limiter/build.gradle)
