package com.github.cybellereaper.medusae.discord.rest;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RateLimiterTest {
    @Test
    void acquireDoesNotBlockWhenTokensRemain() {
        RateLimiter limiter = new RateLimiter(Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));
        limiter.update("GET /x", 1, Instant.parse("2026-01-01T00:00:10Z"));

        assertDoesNotThrow(() -> limiter.acquire("GET /x"));
    }
}
