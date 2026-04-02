package io.github.jho951.ratelimiter.core;

import java.util.concurrent.ConcurrentHashMap;

/**
 * v1: In-memory token bucket store.
 * - 분산 환경에서는 v2에서 Redis/DB store로 교체 권장
 */
public final class InMemoryTokenBucketStore implements TokenBucketStore {
    private final ConcurrentHashMap<String, TokenBucketState> map = new ConcurrentHashMap<>();

    @Override
    public TokenBucketState getOrCreate(String key, long capacity, long now) {
        return map.computeIfAbsent(key, k -> new TokenBucketState(capacity, now));
    }

    /**
     * 선택 기능: 장시간 미사용 버킷 정리.
     * (스케줄러/관리 API 등에서 호출 가능)
     */
    @Override
    public int evictIdle(long now, long idleNanos) {
        int before = map.size();
        map.entrySet().removeIf(e -> (now - e.getValue().lastAccessNanos) > idleNanos);
        return before - map.size();
    }
}
