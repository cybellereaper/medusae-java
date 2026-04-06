package com.github.cybellereaper.examples.commands;

import com.github.cybellereaper.client.ResolvedMember;
import com.github.cybellereaper.client.ResolvedUser;
import com.github.cybellereaper.commands.core.annotation.*;
import com.github.cybellereaper.commands.core.execute.CommandContext;

import java.util.List;

@Command("user")
@Description("User management commands")
public final class UserCommands {

    @Subcommand("ban")
    @Description("Ban a member from the server")
    @RequireUserPermissions("ban_members")
    public void ban(CommandContext ctx, @Name("target") ResolvedMember target, @Optional String reason) {
        String effectiveReason = reason == null ? "No reason provided" : reason;
        ctx.reply("Banned " + (target == null ? "unknown" : target.userId()) + " for " + effectiveReason);
    }

    @Subcommand("info")
    @Description("Show information about a user")
    public void info(CommandContext ctx, @Name("target") ResolvedUser target) {
        ctx.replyEphemeral("User: " + (target == null ? "unknown" : target.username()));
    }

    @Autocomplete("reason")
    public List<String> reasonSuggestions(CommandContext ctx) {
        return List.of("Spam", "Harassment", "Raid");
    }
}
