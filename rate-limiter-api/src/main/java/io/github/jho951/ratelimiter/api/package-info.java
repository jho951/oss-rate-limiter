/**
 * Rate limiter의 공개 계약을 담는 패키지입니다.
 *
 * <p>이 패키지는 key, plan, decision, resolver와 같은 외부 노출 타입만 포함하며,
 * 구현 전략은 core, config, redis 모듈에서 담당합니다.</p>
 */
package io.github.jho951.ratelimiter.api;
