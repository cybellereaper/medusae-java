package com.github.cybellereaper.medusae.discord.client;

import com.github.cybellereaper.medusae.discord.api.DiscordClient;
import com.github.cybellereaper.medusae.discord.commands.Command;
import com.github.cybellereaper.medusae.discord.commands.CommandRegistry;
import com.github.cybellereaper.medusae.discord.events.DiscordEvent;
import com.github.cybellereaper.medusae.discord.events.EventDispatcher;
import com.github.cybellereaper.medusae.discord.events.MessageCreateEvent;
import com.github.cybellereaper.medusae.discord.gateway.DiscordGatewayClient;
import com.github.cybellereaper.medusae.discord.model.DiscordMessage;
import com.github.cybellereaper.medusae.discord.model.MessageCreateRequest;
import com.github.cybellereaper.medusae.discord.rest.DiscordRestClient;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

final class DefaultDiscordClient implements DiscordClient {
    private final DiscordRestClient restClient;
    private final DiscordGatewayClient gatewayClient;
    private final EventDispatcher dispatcher;
    private final CommandRegistry commandRegistry;
    private final PrefixCommandRouter commandRouter;
    private final ExecutorService eventExecutor;

    DefaultDiscordClient(
            DiscordRestClient restClient,
            DiscordGatewayClient gatewayClient,
            EventDispatcher dispatcher,
            CommandRegistry commandRegistry,
            PrefixCommandRouter commandRouter,
            ExecutorService eventExecutor
    ) {
        this.restClient = Objects.requireNonNull(restClient, "restClient");
        this.gatewayClient = Objects.requireNonNull(gatewayClient, "gatewayClient");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.commandRegistry = Objects.requireNonNull(commandRegistry, "commandRegistry");
        this.commandRouter = Objects.requireNonNull(commandRouter, "commandRouter");
        this.eventExecutor = Objects.requireNonNull(eventExecutor, "eventExecutor");

        dispatcher.on(MessageCreateEvent.class, event -> commandRouter.handle(event, this));
    }

    @Override
    public void connect() {
        gatewayClient.connect();
    }

    @Override
    public DiscordMessage sendMessage(String channelId, MessageCreateRequest request) {
        return restClient.sendMessage(channelId, request);
    }

    @Override
    public <T extends DiscordEvent> void onEvent(Class<T> type, Consumer<T> listener) {
        dispatcher.on(type, listener);
    }

    @Override
    public void registerCommand(String name, Command command) {
        commandRegistry.register(name, command);
    }

    @Override
    public void close() {
        gatewayClient.close();
        eventExecutor.shutdownNow();
    }
}
