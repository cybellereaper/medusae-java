package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class InteractionContext {
    private static final String DATA_FIELD = "data";
    private static final String OPTIONS_FIELD = "options";
    private static final String COMPONENTS_FIELD = "components";
    private static final String NAME_FIELD = "name";
    private static final String VALUE_FIELD = "value";
    private static final String CUSTOM_ID_FIELD = "custom_id";
    private static final int MAX_AUTOCOMPLETE_CHOICES = 25;

    private final JsonNode interaction;
    private final SlashCommandRouter.InteractionResponder responder;

    private InteractionContext(JsonNode interaction, SlashCommandRouter.InteractionResponder responder) {
        this.interaction = Objects.requireNonNull(interaction, "interaction");
        this.responder = Objects.requireNonNull(responder, "responder");
    }

    static InteractionContext from(JsonNode interaction, SlashCommandRouter.InteractionResponder responder) {
        return new InteractionContext(interaction, responder);
    }

    public JsonNode raw() {
        return interaction;
    }

    public String id() {
        return interaction.path("id").asText("");
    }

    public String token() {
        return interaction.path("token").asText("");
    }

    public String commandName() {
        return textOrNull(interaction.path(DATA_FIELD).path(NAME_FIELD));
    }

    public String customId() {
        return textOrNull(interaction.path(DATA_FIELD).path(CUSTOM_ID_FIELD));
    }

    public String optionString(String optionName) {
        Objects.requireNonNull(optionName, "optionName");
        JsonNode option = findOptionNode(optionName, interaction.path(DATA_FIELD).path(OPTIONS_FIELD));
        return option == null ? null : textOrNull(option.path(VALUE_FIELD));
    }

    public String modalValue(String customId) {
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
                if (customId.equals(component.path(CUSTOM_ID_FIELD).asText())) {
                    return textOrNull(component.path(VALUE_FIELD));
                }
            }
        }
        return null;
    }

    public void respondWithMessage(String content) {
        respondWithMessage(DiscordMessage.ofContent(content));
    }

    public void respondWithMessage(DiscordMessage message) {
        Objects.requireNonNull(message, "message");
        respond(4, message.toPayload());
    }

    public void respondWithEmbeds(String content, List<DiscordEmbed> embeds) {
        respondWithMessage(DiscordMessage.ofEmbeds(content, embeds));
    }

    public void respondEphemeral(String content) {
        respondWithMessage(DiscordMessage.ofContent(content).asEphemeral());
    }

    public void respondEphemeralWithEmbeds(String content, List<DiscordEmbed> embeds) {
        respondWithMessage(DiscordMessage.ofEmbeds(content, embeds).asEphemeral());
    }

    public void respondWithModal(DiscordModal modal) {
        Objects.requireNonNull(modal, "modal");
        respond(9, modal.toPayload());
    }

    public void respondWithAutocompleteChoices(List<AutocompleteChoice> choices) {
        Objects.requireNonNull(choices, "choices");
        if (choices.size() > MAX_AUTOCOMPLETE_CHOICES) {
            throw new IllegalArgumentException("choices must contain at most " + MAX_AUTOCOMPLETE_CHOICES + " entries");
        }
        respond(8, Map.of("choices", choices.stream().map(AutocompleteChoice::toPayload).toList()));
    }

    public void deferMessage() {
        respond(5, null);
    }

    public void deferUpdate() {
        respond(6, null);
    }

    private void respond(int type, Map<String, Object> data) {
        String interactionId = id();
        String interactionToken = token();
        if (interactionId.isBlank() || interactionToken.isBlank()) {
            throw new IllegalArgumentException("interaction must include id and token");
        }
        responder.respond(interactionId, interactionToken, type, data);
    }

    private static JsonNode findOptionNode(String optionName, JsonNode options) {
        if (!options.isArray()) {
            return null;
        }
        for (JsonNode option : options) {
            if (optionName.equals(option.path(NAME_FIELD).asText())) {
                return option;
            }
            JsonNode nested = findOptionNode(optionName, option.path(OPTIONS_FIELD));
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private static String textOrNull(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        String text = node.asText();
        return text.isBlank() ? null : text;
    }
}
