package com.github.cybellereaper.medusae.discord.rest;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

final class RateLimitBucket {
    private final AtomicInteger remaining = new AtomicInteger(Integer.MAX_VALUE);
    private final AtomicReference<Instant> resetAt = new AtomicReference<>(Instant.EPOCH);

    int remaining() { return remaining.get(); }
    Instant resetAt() { return resetAt.get(); }

    void update(int remaining, Instant resetAt) {
        this.remaining.set(remaining);
        this.resetAt.set(resetAt);
    }
}
