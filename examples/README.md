# Examples

이 저장소의 예시는 공개 계약과 어긋나지 않도록 테스트와 가까운 위치에서 관리합니다.
프레임워크 연동 같은 2계층 예시는 이 저장소 밖에서 다루는 것을 권장합니다.

## 컴파일로 검증되는 예시

- [`RateLimiterSpiContractTest`](../rate-limiter-spi/src/test/java/io/github/jho951/ratelimiter/spi/RateLimiterSpiContractTest.java)
  `resolver`, `policy builder`, 람다 기반 limiter를 이용한 최소 SPI 사용 흐름입니다.
- [`RateLimitValueObjectTest`](../rate-limiter-core/src/test/java/io/github/jho951/ratelimiter/core/RateLimitValueObjectTest.java)
  공개 core 모델 타입의 값 객체 사용 패턴입니다.

## 최소 SPI 흐름

```java
record RequestContext(String clientIp) {}

RateLimitKeyResolver<RequestContext> keyResolver =
    (source, type) -> RateLimitKey.of(type, source.clientIp());

RateLimitPolicyResolver<RequestContext> policyResolver =
    source -> List.of(
        RateLimitPolicy.of(
            "per-ip",
            keyResolver.resolve(source, RateLimitKeyType.IP),
            RateLimitPlan.perSecond(10, 5.0),
            1
        )
    );

RateLimiter limiter = (key, permits, plan) ->
    permits <= plan.getCapacity()
        ? RateLimitDecision.allow(plan.getCapacity() - permits)
        : RateLimitDecision.deny(0, 1000);

RequestContext request = new RequestContext("203.0.113.10");
RateLimitPolicy policy = policyResolver.resolve(request).get(0);
RateLimitDecision decision = limiter.tryAcquire(
    policy.getKey(),
    policy.getPermits(),
    policy.getPlan()
);
```

이 예시는 `core`와 `spi`만으로 구성한 최소 흐름입니다.
Servlet Filter, Spring MVC interceptor, Redis adapter 같은 소비 예시는 별도 2계층에서 두는 편이 저장소 경계에 더 잘 맞습니다.
