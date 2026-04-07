package com.github.cybellereaper.medusae.discord.model;

public record DiscordMessage(String id, String channelId, String content, DiscordUser author) {
}
