package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

final class SlashCommandRouter {
    private static final int APPLICATION_COMMAND_INTERACTION_TYPE = 2;

    private final Map<String, Consumer<JsonNode>> handlers = new ConcurrentHashMap<>();
    private final InteractionResponder responder;

    SlashCommandRouter(InteractionResponder responder) {
        this.responder = Objects.requireNonNull(responder, "responder");
    }

    void registerHandler(String commandName, Consumer<JsonNode> handler) {
        validateCommandName(commandName);
        Objects.requireNonNull(handler, "handler");

        Consumer<JsonNode> previous = handlers.putIfAbsent(commandName, handler);
        if (previous != null) {
            throw new IllegalArgumentException("Slash command already registered: " + commandName);
        }
    }

    void handleInteraction(JsonNode interaction) {
        if (interaction == null || interaction.path("type").asInt() != APPLICATION_COMMAND_INTERACTION_TYPE) {
            return;
        }

        JsonNode data = interaction.path("data");
        String commandName = data.path("name").asText("");

        Consumer<JsonNode> handler = handlers.get(commandName);
        if (handler == null) {
            return;
        }

        handler.accept(interaction);
    }

    void respondWithMessage(JsonNode interaction, String content) {
        Objects.requireNonNull(interaction, "interaction");

        String interactionId = interaction.path("id").asText();
        String interactionToken = interaction.path("token").asText();

        if (interactionId.isBlank() || interactionToken.isBlank()) {
            throw new IllegalArgumentException("interaction must include id and token");
        }

        responder.respondWithMessage(interactionId, interactionToken, content);
    }

    private static void validateCommandName(String commandName) {
        Objects.requireNonNull(commandName, "commandName");
        if (commandName.isBlank()) {
            throw new IllegalArgumentException("commandName must not be blank");
        }
    }

    @FunctionalInterface
    interface InteractionResponder {
        void respondWithMessage(String interactionId, String interactionToken, String content);
    }
}
