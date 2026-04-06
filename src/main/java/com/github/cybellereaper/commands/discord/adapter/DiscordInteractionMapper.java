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
import java.util.Objects;
import java.util.Set;

public final class DiscordInteractionMapper {
    public CommandInteraction toCoreInteraction(JsonNode interaction, InteractionContext context) {
        Objects.requireNonNull(interaction, "interaction");
        Objects.requireNonNull(context, "context");

        JsonNode data = interaction.path("data");
        CommandType commandType = mapCommandType(data.path("type").asInt(1));

        Route route = parseOptions(data.path("options"), new HashMap<>(), new Route(null, null));
        Map<String, CommandOptionValue> options = route.options;

        String targetId = textOrNull(data.path("target_id"));
        ResolvedUser targetUser = commandType == CommandType.USER_CONTEXT && targetId != null ? context.resolvedUserValue(targetId) : null;
        ResolvedMember targetMember = commandType == CommandType.USER_CONTEXT && targetId != null ? context.resolvedMemberValue(targetId) : null;
        ResolvedMessage targetMessage = commandType == CommandType.MESSAGE_CONTEXT && targetId != null
                ? ResolvedMessage.from(data.path("resolved").path("messages").path(targetId))
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
                return textOrNull(option.path("name"));
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
            return route.withOptions(options);
        }
        for (JsonNode option : nodes) {
            int type = option.path("type").asInt(0);
            String name = textOrNull(option.path("name"));
            if (name == null) {
                continue;
            }

            if (type == 1) {
                route = new Route(route.group, name, options);
                route = parseOptions(option.path("options"), options, route);
                continue;
            }
            if (type == 2) {
                JsonNode children = option.path("options");
                if (children.isArray() && !children.isEmpty()) {
                    JsonNode subcommandNode = children.get(0);
                    String subcommandName = textOrNull(subcommandNode.path("name"));
                    route = new Route(name, subcommandName, options);
                    route = parseOptions(subcommandNode.path("options"), options, route);
                }
                continue;
            }

            options.put(name, new CommandOptionValue(parseValue(option.path("value")), type));
        }
        return route.withOptions(options);
    }

    private static Object parseValue(JsonNode value) {
        if (value == null || value.isNull() || value.isMissingNode()) {
            return null;
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isIntegralNumber()) {
            return value.asLong();
        }
        if (value.isFloatingPointNumber()) {
            return value.asDouble();
        }
        return textOrNull(value);
    }

    private static String textOrNull(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        String text = node.asText(null);
        return text == null || text.isBlank() ? null : text;
    }

    private record Route(String group, String subcommand, Map<String, CommandOptionValue> options) {
        private Route(String group, String subcommand) {
            this(group, subcommand, Map.of());
        }

        private Route withOptions(Map<String, CommandOptionValue> newOptions) {
            return new Route(group, subcommand, Map.copyOf(newOptions));
        }
    }
}
