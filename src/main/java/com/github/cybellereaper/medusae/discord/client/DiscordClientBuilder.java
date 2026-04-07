package com.github.cybellereaper.medusae.discord.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.medusae.discord.api.DiscordClient;
import com.github.cybellereaper.medusae.discord.commands.CommandRegistry;
import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.events.EventDispatcher;
import com.github.cybellereaper.medusae.discord.gateway.DiscordGatewayClient;
import com.github.cybellereaper.medusae.discord.gateway.GatewayTransportFactory;
import com.github.cybellereaper.medusae.discord.gateway.JdkGatewayTransportFactory;
import com.github.cybellereaper.medusae.discord.rest.DiscordRestClient;
import com.github.cybellereaper.medusae.discord.serialization.JacksonJsonCodec;
import com.github.cybellereaper.medusae.discord.serialization.JsonCodec;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DiscordClientBuilder {
    private final DiscordClientConfig config;
    private HttpClient httpClient = HttpClient.newHttpClient();
    private JsonCodec codec = new JacksonJsonCodec(new ObjectMapper());
    private GatewayTransportFactory gatewayTransportFactory;
    private String commandPrefix = "!";

    private DiscordClientBuilder(DiscordClientConfig config) {
        this.config = config;
    }

    public static DiscordClientBuilder create(DiscordClientConfig config) {
        return new DiscordClientBuilder(config);
    }

    public DiscordClientBuilder httpClient(HttpClient httpClient) { this.httpClient = httpClient; return this; }
    public DiscordClientBuilder codec(JsonCodec codec) { this.codec = codec; return this; }
    public DiscordClientBuilder gatewayTransportFactory(GatewayTransportFactory factory) { this.gatewayTransportFactory = factory; return this; }
    public DiscordClientBuilder commandPrefix(String commandPrefix) { this.commandPrefix = commandPrefix; return this; }

    public DiscordClient build() {
        ExecutorService eventExecutor = Executors.newVirtualThreadPerTaskExecutor();
        EventDispatcher dispatcher = new EventDispatcher(eventExecutor);
        DiscordRestClient restClient = new DiscordRestClient(httpClient, config, codec);
        GatewayTransportFactory factory = gatewayTransportFactory == null ? new JdkGatewayTransportFactory(httpClient) : gatewayTransportFactory;
        DiscordGatewayClient gatewayClient = new DiscordGatewayClient(config, codec, factory, dispatcher);
        CommandRegistry commandRegistry = new CommandRegistry();
        PrefixCommandRouter router = new PrefixCommandRouter(commandPrefix, commandRegistry);
        return new DefaultDiscordClient(restClient, gatewayClient, dispatcher, commandRegistry, router, eventExecutor);
    }
}
