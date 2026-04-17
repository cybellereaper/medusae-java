package com.github.cybellereaper.medusae.sdk.core.runtime;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotRuntimeConfigTest {

    @Test
    void shouldRejectBlankToken() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> BotRuntimeConfig.builder()
                .token("  ")
                .build());

        assertTrue(ex.getMessage().contains("token"));
    }

    @Test
    void shouldRejectInvalidShardCount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> BotRuntimeConfig.builder()
                .token("abc")
                .shardCount(0)
                .build());

        assertEquals("shardCount must be >= 1", ex.getMessage());
    }

    @Test
    void shouldBuildValidConfig() {
        BotRuntimeConfig config = BotRuntimeConfig.builder()
                .token("abc")
                .automaticSharding(false)
                .shardCount(2)
                .shutdownTimeout(Duration.ofSeconds(45))
                .build();

        assertEquals("abc", config.botToken());
        assertEquals(2, config.shardCount());
        assertEquals(Duration.ofSeconds(45), config.shutdownTimeout());
        assertTrue(!config.automaticSharding());
    }
}
