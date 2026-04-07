package com.github.cybellereaper.medusae.discord.model;

import java.util.Objects;

public record MessageCreateRequest(String content) {
    public MessageCreateRequest {
        Objects.requireNonNull(content, "content");
        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
    }
}
