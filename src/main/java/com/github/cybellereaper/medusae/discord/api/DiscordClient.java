package com.github.cybellereaper.medusae.discord.api;

import com.github.cybellereaper.medusae.discord.commands.Command;
import com.github.cybellereaper.medusae.discord.events.DiscordEvent;
import com.github.cybellereaper.medusae.discord.model.DiscordMessage;
import com.github.cybellereaper.medusae.discord.model.MessageCreateRequest;

import java.util.function.Consumer;

public interface DiscordClient extends AutoCloseable {
    void connect();
    DiscordMessage sendMessage(String channelId, MessageCreateRequest request);
    <T extends DiscordEvent> void onEvent(Class<T> type, Consumer<T> listener);
    void registerCommand(String name, Command command);
    @Override
    void close();
}
