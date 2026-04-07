package com.github.cybellereaper.medusae.discord.client;

import com.github.cybellereaper.medusae.discord.api.DiscordClient;
import com.github.cybellereaper.medusae.discord.model.DiscordMessage;

public record CommandContext(DiscordMessage message, DiscordClient client, String[] args) {
}
