package com.github.cybellereaper.medusae.discord.rest;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RateLimiter {
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private final Clock clock;

    public RateLimiter(Clock clock) {
        this.clock = clock;
    }

    public void acquire(String bucketKey) {
        RateLimitBucket bucket = buckets.get(bucketKey);
        if (bucket == null) {
            return;
        }
        if (bucket.remaining() > 0) {
            return;
        }
        Duration wait = Duration.between(clock.instant(), bucket.resetAt());
        if (!wait.isNegative() && !wait.isZero()) {
            try {
                Thread.sleep(wait.toMillis());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void update(String bucketKey, int remaining, Instant resetAt) {
        buckets.computeIfAbsent(bucketKey, ignored -> new RateLimitBucket()).update(remaining, resetAt);
    }
}
