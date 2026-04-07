package com.github.cybellereaper.medusae.discord.events;

import com.github.cybellereaper.medusae.discord.model.DiscordMessage;

public record MessageCreateEvent(DiscordMessage message) implements DiscordEvent {
    @Override
    public String type() {
        return "MESSAGE_CREATE";
    }
}
