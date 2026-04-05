package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

final class SlashCommandRouter {
    private static final String DATA_FIELD = "data";
    private static final String ID_FIELD = "id";
    private static final String TOKEN_FIELD = "token";
    private static final String OPTIONS_FIELD = "options";
    private static final String COMPONENTS_FIELD = "components";
    private static final String VALUE_FIELD = "value";

    private final Map<String, Consumer<JsonNode>> slashHandlers = new ConcurrentHashMap<>();
    private final Map<String, Consumer<JsonNode>> userContextMenuHandlers = new ConcurrentHashMap<>();
    private final Map<String, Consumer<JsonNode>> messageContextMenuHandlers = new ConcurrentHashMap<>();
    private final Map<String, Consumer<JsonNode>> componentHandlers = new ConcurrentHashMap<>();
    private final Map<String, Consumer<JsonNode>> modalHandlers = new ConcurrentHashMap<>();
    private final Map<String, Consumer<JsonNode>> autocompleteHandlers = new ConcurrentHashMap<>();
    private final InteractionResponder responder;

    SlashCommandRouter(InteractionResponder responder) {
        this.responder = Objects.requireNonNull(responder, "responder");
    }

    void registerSlashHandler(String commandName, Consumer<JsonNode> handler) {
        registerUniqueHandler(slashHandlers, commandName, "slash command", handler);
    }

    void registerComponentHandler(String customId, Consumer<JsonNode> handler) {
        registerUniqueHandler(componentHandlers, customId, "component", handler);
    }

    void registerModalHandler(String customId, Consumer<JsonNode> handler) {
        registerUniqueHandler(modalHandlers, customId, "modal", handler);
    }

    void registerAutocompleteHandler(String commandName, Consumer<JsonNode> handler) {
        registerUniqueHandler(autocompleteHandlers, commandName, "autocomplete", handler);
    }

    void registerUserContextMenuHandler(String commandName, Consumer<JsonNode> handler) {
        registerUniqueHandler(userContextMenuHandlers, commandName, "user context menu", handler);
    }

    void registerMessageContextMenuHandler(String commandName, Consumer<JsonNode> handler) {
        registerUniqueHandler(messageContextMenuHandlers, commandName, "message context menu", handler);
    }

    void handleInteraction(JsonNode interaction) {
        if (interaction == null) {
            return;
        }

        InteractionType interactionType = InteractionType.fromCode(interaction.path("type").asInt());
        switch (interactionType) {
            case PING -> respond(interaction, ResponseType.PONG, null);
            case APPLICATION_COMMAND -> handleApplicationCommand(interaction);
            case APPLICATION_COMMAND_AUTOCOMPLETE -> dispatchByDataField(interaction, autocompleteHandlers, DataField.NAME);
            case MESSAGE_COMPONENT -> dispatchByDataField(interaction, componentHandlers, DataField.CUSTOM_ID);
            case MODAL_SUBMIT -> dispatchByDataField(interaction, modalHandlers, DataField.CUSTOM_ID);
            case UNKNOWN -> {
                // Unknown interaction types are intentionally ignored.
            }
        }
    }

    void respondWithMessage(JsonNode interaction, String content) {
        respondWithMessage(interaction, DiscordMessage.ofContent(content));
    }

    void respondWithMessage(JsonNode interaction, DiscordMessage message) {
        Objects.requireNonNull(message, "message");
        respond(interaction, ResponseType.CHANNEL_MESSAGE, message.toPayload());
    }

    void respondWithEmbeds(JsonNode interaction, String content, List<DiscordEmbed> embeds) {
        respondWithMessage(interaction, DiscordMessage.ofEmbeds(content, embeds));
    }

    void respondEphemeral(JsonNode interaction, String content) {
        respondWithMessage(interaction, DiscordMessage.ofContent(content).asEphemeral());
    }

    void respondEphemeralWithEmbeds(JsonNode interaction, String content, List<DiscordEmbed> embeds) {
        respondWithMessage(interaction, DiscordMessage.ofEmbeds(content, embeds).asEphemeral());
    }

    void respondWithModal(JsonNode interaction, DiscordModal modal) {
        Objects.requireNonNull(modal, "modal");
        respond(interaction, ResponseType.MODAL, modal.toPayload());
    }

    void respondWithAutocompleteChoices(JsonNode interaction, List<AutocompleteChoice> choices) {
        Objects.requireNonNull(choices, "choices");

        respond(interaction, ResponseType.AUTOCOMPLETE, Map.of(
                "choices", choices.stream().map(AutocompleteChoice::toPayload).toList()
        ));
    }

    void deferMessage(JsonNode interaction) {
        respond(interaction, ResponseType.DEFERRED_CHANNEL_MESSAGE, null);
    }

    void deferUpdate(JsonNode interaction) {
        respond(interaction, ResponseType.DEFERRED_MESSAGE_UPDATE, null);
    }

    String getOptionString(JsonNode interaction, String optionName) {
        Objects.requireNonNull(interaction, "interaction");
        Objects.requireNonNull(optionName, "optionName");

        JsonNode options = interaction.path(DATA_FIELD).path(OPTIONS_FIELD);
        if (!options.isArray()) {
            return null;
        }

        for (JsonNode option : options) {
            if (optionName.equals(option.path(DataField.NAME.value()).asText())) {
                return valueAsTextOrNull(option.path(VALUE_FIELD));
            }
        }

        return null;
    }

    String getModalValue(JsonNode interaction, String customId) {
        Objects.requireNonNull(interaction, "interaction");
        Objects.requireNonNull(customId, "customId");

        JsonNode rows = interaction.path(DATA_FIELD).path(COMPONENTS_FIELD);
        if (!rows.isArray()) {
            return null;
        }

        for (JsonNode row : rows) {
            JsonNode components = row.path(COMPONENTS_FIELD);
            if (!components.isArray()) {
                continue;
            }

            for (JsonNode component : components) {
                if (customId.equals(component.path(DataField.CUSTOM_ID.value()).asText())) {
                    return valueAsTextOrNull(component.path(VALUE_FIELD));
                }
            }
        }

        return null;
    }

    private void handleApplicationCommand(JsonNode interaction) {
        CommandType commandType = CommandType.fromCode(
                interaction.path(DATA_FIELD).path("type").asInt(CommandType.CHAT_INPUT.code())
        );

        Map<String, Consumer<JsonNode>> handlers = switch (commandType) {
            case USER_CONTEXT -> userContextMenuHandlers;
            case MESSAGE_CONTEXT -> messageContextMenuHandlers;
            case CHAT_INPUT, UNKNOWN -> slashHandlers;
        };

        dispatchByDataField(interaction, handlers, DataField.NAME);
    }

    private void dispatchByDataField(JsonNode interaction, Map<String, Consumer<JsonNode>> handlers, DataField dataField) {
        String handlerKey = interaction.path(DATA_FIELD).path(dataField.value()).asText("");
        Consumer<JsonNode> handler = handlers.get(handlerKey);
        if (handler != null) {
            handler.accept(interaction);
        }
    }

    private void respond(JsonNode interaction, ResponseType responseType, Map<String, Object> data) {
        Objects.requireNonNull(interaction, "interaction");

        String interactionId = interaction.path(ID_FIELD).asText();
        String interactionToken = interaction.path(TOKEN_FIELD).asText();

        if (interactionId.isBlank() || interactionToken.isBlank()) {
            throw new IllegalArgumentException("interaction must include id and token");
        }

        responder.respond(interactionId, interactionToken, responseType.code(), data);
    }

    private static String valueAsTextOrNull(JsonNode valueNode) {
        return valueNode.isMissingNode() || valueNode.isNull() ? null : valueNode.asText();
    }

    private static void registerUniqueHandler(
            Map<String, Consumer<JsonNode>> handlers,
            String key,
            String handlerType,
            Consumer<JsonNode> handler
    ) {
        validateKey(key, handlerType);
        Objects.requireNonNull(handler, "handler");

        Consumer<JsonNode> previous = handlers.putIfAbsent(key, handler);
        if (previous != null) {
            throw new IllegalArgumentException("Interaction handler already registered for " + handlerType + ": " + key);
        }
    }

    private static void validateKey(String key, String keyType) {
        Objects.requireNonNull(key, keyType + " key");
        if (key.isBlank()) {
            throw new IllegalArgumentException(keyType + " key must not be blank");
        }
    }

    private enum InteractionType {
        PING(1),
        APPLICATION_COMMAND(2),
        MESSAGE_COMPONENT(3),
        APPLICATION_COMMAND_AUTOCOMPLETE(4),
        MODAL_SUBMIT(5),
        UNKNOWN(-1);

        private static final Map<Integer, InteractionType> BY_CODE = byCode(values(), InteractionType::code);

        private final int code;

        InteractionType(int code) {
            this.code = code;
        }

        int code() {
            return code;
        }

        private static InteractionType fromCode(int code) {
            return BY_CODE.getOrDefault(code, UNKNOWN);
        }
    }

    private enum CommandType {
        CHAT_INPUT(1),
        USER_CONTEXT(2),
        MESSAGE_CONTEXT(3),
        UNKNOWN(-1);

        private static final Map<Integer, CommandType> BY_CODE = byCode(values(), CommandType::code);

        private final int code;

        CommandType(int code) {
            this.code = code;
        }

        int code() {
            return code;
        }

        private static CommandType fromCode(int code) {
            return BY_CODE.getOrDefault(code, UNKNOWN);
        }
    }

    private enum ResponseType {
        PONG(1),
        CHANNEL_MESSAGE(4),
        DEFERRED_CHANNEL_MESSAGE(5),
        DEFERRED_MESSAGE_UPDATE(6),
        AUTOCOMPLETE(8),
        MODAL(9);

        private final int code;

        ResponseType(int code) {
            this.code = code;
        }

        int code() {
            return code;
        }
    }

    private enum DataField {
        NAME("name"),
        CUSTOM_ID("custom_id");

        private final String value;

        DataField(String value) {
            this.value = value;
        }

        String value() {
            return value;
        }
    }

    private static <T> Map<Integer, T> byCode(T[] values, Function<T, Integer> keyMapper) {
        return Arrays.stream(values).collect(Collectors.toUnmodifiableMap(keyMapper, Function.identity()));
    }

    @FunctionalInterface
    interface InteractionResponder {
        void respond(String interactionId, String interactionToken, int type, Map<String, Object> data);
    }
}
