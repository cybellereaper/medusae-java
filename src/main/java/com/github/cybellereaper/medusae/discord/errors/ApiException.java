package com.github.cybellereaper.medusae.discord.errors;

public final class ApiException extends DiscordException {
    private final int status;

    public ApiException(int status, String body) {
        super("Discord API call failed with status " + status + ": " + body);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
