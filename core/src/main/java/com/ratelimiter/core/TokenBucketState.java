package com.ratelimiter.core;

public final class TokenBucketState {
    double tokens;
    long lastRefillNanos;
    long lastAccessNanos;

    public TokenBucketState(double tokens, long now) {
        this.tokens = tokens;
        this.lastRefillNanos = now;
        this.lastAccessNanos = now;
    }
}
