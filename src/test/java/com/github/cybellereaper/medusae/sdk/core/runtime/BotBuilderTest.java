package com.github.cybellereaper.medusae.sdk.core.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BotBuilderTest {

    @Test
    void shouldCreateRuntimeFromBuilder() {
        BotRuntime runtime = DiscordSdk.builder()
                .token("token")
                .automaticSharding(false)
                .shardCount(3)
                .build();

        assertEquals("token", runtime.config().botToken());
        assertEquals(3, runtime.config().shardCount());
    }
}
