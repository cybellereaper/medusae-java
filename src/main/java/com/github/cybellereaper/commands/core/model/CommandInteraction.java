package com.github.cybellereaper.commands.core.model;

import java.util.Map;
import java.util.Set;

public record CommandInteraction(
        String commandName,
        CommandType commandType,
        String subcommandGroup,
        String subcommand,
        Map<String, CommandOptionValue> options,
        String focusedOption,
        Object rawInteraction,
        boolean dm,
        String guildId,
        String userId,
        Set<String> userPermissions,
        Set<String> botPermissions,
        Object targetUser,
        Object targetMember,
        Object targetChannel,
        Object targetRole,
        Object targetAttachment,
        Object targetMessage
) {
    public CommandInteraction {
        options = options == null ? Map.of() : Map.copyOf(options);
        userPermissions = userPermissions == null ? Set.of() : Set.copyOf(userPermissions);
        botPermissions = botPermissions == null ? Set.of() : Set.copyOf(botPermissions);
    }

    public String routeKey() {
        if (subcommandGroup != null && subcommand != null) {
            return subcommandGroup + "/" + subcommand;
        }
        return subcommand;
    }
}
