package com.github.cybellereaper.medusae.discord.commands;

import com.github.cybellereaper.medusae.discord.client.CommandContext;

@FunctionalInterface
public interface Command {
    void execute(CommandContext context);
}
