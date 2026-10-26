package io.github.jho951.ratelimiter.core;

/** 요청 주체의 분류 */
public enum RateLimitKeyType {
    /** 클라이언트 IP 주소 기준 식별자입니다. */
    IP,
    /** 사용자 ID 기준 식별자입니다. */
    USER_ID,
    /** API 키 기준 식별자입니다. */
    API_KEY,
	/** 호출자가 정의한 사용자 지정 식별자입니다. */
	CUSTOM
}
