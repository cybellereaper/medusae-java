package com.github.cybellereaper.medusae.discord.client;

import com.github.cybellereaper.medusae.discord.api.DiscordClient;
import com.github.cybellereaper.medusae.discord.commands.CommandRegistry;
import com.github.cybellereaper.medusae.discord.events.MessageCreateEvent;

import java.util.Objects;

final class PrefixCommandRouter {
    private final String prefix;
    private final CommandRegistry registry;

    PrefixCommandRouter(String prefix, CommandRegistry registry) {
        this.prefix = Objects.requireNonNull(prefix, "prefix");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    void handle(MessageCreateEvent event, DiscordClient client) {
        String content = event.message().content();
        if (content == null || !content.startsWith(prefix)) {
            return;
        }

        String[] parts = content.substring(prefix.length()).trim().split("\\s+");
        if (parts.length == 0 || parts[0].isBlank()) {
            return;
        }

        String command = parts[0];
        String[] args = java.util.Arrays.copyOfRange(parts, 1, parts.length);
        registry.find(command).ifPresent(handler -> handler.execute(new CommandContext(event.message(), client, args)));
    }
}
