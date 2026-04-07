package com.github.cybellereaper.medusae.discord.auth;

import java.util.Objects;

public record BotToken(String value) {
    public BotToken {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Bot token must not be blank");
        }
    }

    public String asAuthorizationHeader() {
        return "Bot " + value;
    }
}
