package com.github.cybellereaper.medusae.discord.errors;

public final class GatewayException extends DiscordException {
    public GatewayException(String message) {
        super(message);
    }

    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
