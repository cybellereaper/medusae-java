package com.github.cybellereaper.examples.commands;

import com.github.cybellereaper.commands.core.execute.CommandFramework;

public final class ExampleCommandBootstrap {
    private ExampleCommandBootstrap() {
    }

    public static CommandFramework createFramework() {
        CommandFramework framework = new CommandFramework();
        framework.registerCheck("guildonly", ctx -> !ctx.interaction().dm());
        framework.registerAutocomplete("membersearch", (ctx, value) -> java.util.List.of(value + "-1", value + "-2"));
        framework.registerCommands(new UserCommands());
        return framework;
    }
}
