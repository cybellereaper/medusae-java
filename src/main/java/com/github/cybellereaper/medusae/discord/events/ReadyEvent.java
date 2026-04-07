package com.github.cybellereaper.medusae.discord.events;

public record ReadyEvent(String sessionId, String resumeGatewayUrl) implements DiscordEvent {
    @Override
    public String type() {
        return "READY";
    }
}
