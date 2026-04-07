package com.github.cybellereaper.medusae.discord.events;

public record UnknownDispatchEvent(String type, String rawJson) implements DiscordEvent {
}
