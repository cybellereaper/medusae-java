package com.github.cybellereaper.commands.discord.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cybellereaper.client.AutocompleteChoice;
import com.github.cybellereaper.client.InteractionContext;
import com.github.cybellereaper.commands.core.execute.CommandFramework;
import com.github.cybellereaper.commands.discord.response.DiscordResponseApplier;

import java.util.List;

public final class DiscordCommandDispatcher {
    private final CommandFramework framework;
    private final DiscordInteractionMapper mapper;

    public DiscordCommandDispatcher(CommandFramework framework) {
        this.framework = framework;
        this.mapper = new DiscordInteractionMapper();
    }

    public void dispatch(JsonNode interaction, InteractionContext interactionContext) {
        var coreInteraction = mapper.toCoreInteraction(interaction, interactionContext);
        framework.execute(coreInteraction, new DiscordResponseApplier(interactionContext));
    }

    public void dispatchAutocomplete(JsonNode interaction, InteractionContext interactionContext) {
        var coreInteraction = mapper.toCoreInteraction(interaction, interactionContext);
        List<String> suggestions = framework.executeAutocomplete(coreInteraction, new DiscordResponseApplier(interactionContext));
        List<AutocompleteChoice> choices = suggestions.stream().limit(25).map(value -> new AutocompleteChoice(value, value)).toList();
        interactionContext.respondWithAutocompleteChoices(choices);
    }
}
