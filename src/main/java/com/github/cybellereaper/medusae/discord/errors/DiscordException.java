package com.github.cybellereaper.medusae.discord.errors;

public sealed class DiscordException extends RuntimeException
        permits TransportException, RateLimitException, GatewayException, SerializationException, ApiException {
    public DiscordException(String message) {
        super(message);
    }

    public DiscordException(String message, Throwable cause) {
        super(message, cause);
    }
}
