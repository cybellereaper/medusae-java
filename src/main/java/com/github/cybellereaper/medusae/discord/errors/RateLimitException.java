package com.github.cybellereaper.medusae.discord.errors;

public final class RateLimitException extends DiscordException {
    public RateLimitException(String message) {
        super(message);
    }
}
