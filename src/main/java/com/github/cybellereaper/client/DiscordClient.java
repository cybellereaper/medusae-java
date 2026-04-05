package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.gateway.DiscordGatewayClient;
import com.github.cybellereaper.http.DiscordRestClient;

import java.net.http.HttpClient;
import java.util.function.Consumer;

public final class DiscordClient implements AutoCloseable {
    private final DiscordRestClient restClient;
    private final DiscordGatewayClient gatewayClient;
    private final SlashCommandRouter slashCommandRouter;

    private DiscordClient(DiscordRestClient restClient, DiscordGatewayClient gatewayClient) {
        this.restClient = restClient;
        this.gatewayClient = gatewayClient;
        this.slashCommandRouter = new SlashCommandRouter(restClient::createInteractionResponse);
        this.gatewayClient.on("INTERACTION_CREATE", slashCommandRouter::handleInteraction);
    }

    public static DiscordClient create(DiscordClientConfig config) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        ObjectMapper objectMapper = new ObjectMapper();

        DiscordRestClient restClient = new DiscordRestClient(httpClient, objectMapper, config);
        DiscordGatewayClient gatewayClient = new DiscordGatewayClient(httpClient, objectMapper, config, restClient);

        return new DiscordClient(restClient, gatewayClient);
    }

    public void login() {
        gatewayClient.connect();
    }

    public void on(String eventType, Consumer<JsonNode> listener) {
        gatewayClient.on(eventType, listener);
    }

    public void onSlashCommand(String commandName, Consumer<JsonNode> listener) {
        slashCommandRouter.registerHandler(commandName, listener);
    }

    public JsonNode registerGlobalSlashCommand(String commandName, String description) {
        String applicationId = restClient.getCurrentApplicationId();
        return restClient.createGlobalApplicationCommand(applicationId, commandName, description);
    }

    public void respondWithMessage(JsonNode interaction, String content) {
        slashCommandRouter.respondWithMessage(interaction, content);
    }

    public void sendMessage(String channelId, String content) {
        restClient.sendMessage(channelId, content);
    }

    @Override
    public void close() {
        gatewayClient.close();
    }
}
