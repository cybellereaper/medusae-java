package com.github.cybellereaper.commands.discord.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cybellereaper.client.InteractionContext;
import com.github.cybellereaper.client.ResolvedMember;
import com.github.cybellereaper.client.ResolvedMessage;
import com.github.cybellereaper.client.ResolvedUser;
import com.github.cybellereaper.commands.core.model.CommandInteraction;
import com.github.cybellereaper.commands.core.model.CommandOptionValue;
import com.github.cybellereaper.commands.core.model.CommandType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DiscordInteractionMapper {
    public CommandInteraction toCoreInteraction(JsonNode interaction, InteractionContext context) {
        JsonNode data = interaction.path("data");
        CommandType commandType = mapCommandType(data.path("type").asInt(1));

        Route route = new Route(null, null);
        Map<String, CommandOptionValue> options = new HashMap<>();
        route = parseOptions(data.path("options"), options, route);

        String targetId = data.path("target_id").asText(null);
        ResolvedUser targetUser = commandType == CommandType.USER_CONTEXT ? context.resolvedUserValue(targetId) : null;
        ResolvedMember targetMember = commandType == CommandType.USER_CONTEXT ? context.resolvedMemberValue(targetId) : null;
        ResolvedMessage targetMessage = commandType == CommandType.MESSAGE_CONTEXT
                ? ResolvedMessage.from(data.path("resolved").path("messages").path(targetId == null ? "" : targetId))
                : null;

        return new CommandInteraction(
                data.path("name").asText(""),
                commandType,
                route.group,
                route.subcommand,
                options,
                focusedOption(data.path("options")),
                interaction,
                context.guildId() == null,
                context.guildId(),
                context.userId(),
                Set.of(),
                Set.of(),
                targetUser,
                targetMember,
                null,
                null,
                null,
                targetMessage
        );
    }

    private static CommandType mapCommandType(int discordType) {
        return switch (discordType) {
            case 2 -> CommandType.USER_CONTEXT;
            case 3 -> CommandType.MESSAGE_CONTEXT;
            default -> CommandType.CHAT_INPUT;
        };
    }

    private static String focusedOption(JsonNode options) {
        if (!options.isArray()) {
            return null;
        }
        for (JsonNode option : options) {
            if (option.path("focused").asBoolean(false)) {
                return option.path("name").asText(null);
            }
            String nested = focusedOption(option.path("options"));
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private static Route parseOptions(JsonNode nodes, Map<String, CommandOptionValue> options, Route seed) {
        Route route = seed;
        if (!nodes.isArray()) {
            return route;
        }
        for (JsonNode option : nodes) {
            int type = option.path("type").asInt(0);
            String name = option.path("name").asText("");
            if (type == 1) {
                route = new Route(route.group, name);
                route = parseOptions(option.path("options"), options, route);
                continue;
            }
            if (type == 2) {
                JsonNode child = option.path("options");
                if (child.isArray() && !child.isEmpty()) {
                    JsonNode subcommandNode = child.get(0);
                    route = new Route(name, subcommandNode.path("name").asText(null));
                    route = parseOptions(subcommandNode.path("options"), options, route);
                }
                continue;
            }
            Object value = parseValue(option.path("value"));
            options.put(name, new CommandOptionValue(value, type));
        }
        return route;
    }

    private static Object parseValue(JsonNode value) {
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isIntegralNumber()) {
            return value.asLong();
        }
        if (value.isFloatingPointNumber()) {
            return value.asDouble();
        }
        return value.asText(null);
    }

    private record Route(String group, String subcommand) {}
}
