package com.github.cybellereaper.medusae.discord.events;

public sealed interface DiscordEvent permits ReadyEvent, MessageCreateEvent, UnknownDispatchEvent {
    String type();
}
