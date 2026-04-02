package io.github.jho951.ratelimiter.core;

/**
 * Token Bucket 상태 저장소.
 *
 * 구현체는 memory, Redis, DB 등으로 교체할 수 있다.
 */
public interface TokenBucketStore {
    TokenBucketState getOrCreate(String key, long capacity, long nowNanos);

    /**
     * 유휴 버킷 정리.
     */
    int evictIdle(long nowNanos, long idleNanos);
}
