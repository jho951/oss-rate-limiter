package io.github.jho951.ratelimiter.core;

/**
 * 시간 소스(테스트 용이).
 */
public interface Clock {
    long nanoTime();

    static Clock system() {
        return System::nanoTime;
    }
}
