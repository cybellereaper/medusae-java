package com.github.cybellereaper.medusae.examples.discord;

import com.github.cybellereaper.medusae.discord.auth.BotToken;
import com.github.cybellereaper.medusae.discord.client.DiscordClientBuilder;
import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.events.MessageCreateEvent;
import com.github.cybellereaper.medusae.discord.model.MessageCreateRequest;

public final class ModernDiscordBotExample {
    private ModernDiscordBotExample() {
    }

    public static void main(String[] args) {
        var config = DiscordClientConfig.builder(new BotToken(System.getenv("DISCORD_BOT_TOKEN")))
                .intents(1 << 9)
                .build();

        try (var client = DiscordClientBuilder.create(config).commandPrefix("!").build()) {
            client.onEvent(MessageCreateEvent.class, event -> {
                if ("ping".equalsIgnoreCase(event.message().content())) {
                    client.sendMessage(event.message().channelId(), new MessageCreateRequest("pong"));
                }
            });

            client.registerCommand("echo", context -> {
                String text = String.join(" ", context.args());
                client.sendMessage(context.message().channelId(), new MessageCreateRequest(text.isBlank() ? "(empty)" : text));
            });

            client.connect();
        }
    }
}
