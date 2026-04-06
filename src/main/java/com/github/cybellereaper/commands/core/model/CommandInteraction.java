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
        Object targetMessage,
        Map<String, Object> optionUsers,
        Map<String, Object> optionMembers,
        Map<String, Object> optionChannels,
        Map<String, Object> optionRoles,
        Map<String, Object> optionAttachments
) {
    public CommandInteraction {
        options = options == null ? Map.of() : Map.copyOf(options);
        userPermissions = userPermissions == null ? Set.of() : Set.copyOf(userPermissions);
        botPermissions = botPermissions == null ? Set.of() : Set.copyOf(botPermissions);
        optionUsers = optionUsers == null ? Map.of() : Map.copyOf(optionUsers);
        optionMembers = optionMembers == null ? Map.of() : Map.copyOf(optionMembers);
        optionChannels = optionChannels == null ? Map.of() : Map.copyOf(optionChannels);
        optionRoles = optionRoles == null ? Map.of() : Map.copyOf(optionRoles);
        optionAttachments = optionAttachments == null ? Map.of() : Map.copyOf(optionAttachments);
    }

    public CommandInteraction(
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
        this(commandName, commandType, subcommandGroup, subcommand, options, focusedOption, rawInteraction, dm, guildId, userId,
                userPermissions, botPermissions, targetUser, targetMember, targetChannel, targetRole, targetAttachment,
                targetMessage, Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
    }

    public String routeKey() {
        if (subcommandGroup != null && subcommand != null) {
            return subcommandGroup + "/" + subcommand;
        }
        return subcommand;
    }
}
