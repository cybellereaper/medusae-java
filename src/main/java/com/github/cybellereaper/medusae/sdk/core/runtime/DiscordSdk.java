package com.github.cybellereaper.medusae.sdk.core.runtime;

public final class DiscordSdk {

    private DiscordSdk() {
    }

    public static BotBuilder builder() {
        return new BotBuilder();
    }
}
