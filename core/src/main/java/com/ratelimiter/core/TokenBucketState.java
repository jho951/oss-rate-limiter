package com.ratelimiter.core;

final class TokenBucketState {
    double tokens;
    long lastRefillNanos;
    long lastAccessNanos;

    TokenBucketState(double tokens, long now) {
        this.tokens = tokens;
        this.lastRefillNanos = now;
        this.lastAccessNanos = now;
    }
}
