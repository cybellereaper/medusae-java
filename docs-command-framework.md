# Annotation Command Framework

This document explains the annotation-first framework under `com.github.cybellereaper.commands`.

Use this framework when you want command modules that scale cleanly (typed options, reusable checks, custom resolvers, and explicit cooldown/permission policies).

## Architecture at a glance

The framework is split into:

- **Core (transport-agnostic)**: parsing annotations, validating metadata, command execution pipeline.
- **Discord adapter layer**: maps Discord interactions into core models and applies framework responses back to Discord.

That separation lets you keep command/business logic isolated from Discord transport wiring.

## Getting started

```java
CommandFramework framework = new CommandFramework();

framework.registerCheck("guildonly", ctx -> !ctx.interaction().dm());
framework.registerAutocomplete("membersearch", (ctx, value) -> List.of("alice", "bob"));
framework.registerCommands(new UserCommands());

DiscordFrameworkBinder binder = new DiscordFrameworkBinder(framework);
binder.bind(discordClient);

DiscordCommandSyncService sync = new DiscordCommandSyncService(framework);
sync.syncGlobal(discordClient);
```

## Annotation model

### Command declaration

- `@Command`: root command (slash or context command)
- `@Subcommand`: slash subcommand handler
- `@SubcommandGroup`: groups related subcommands
- `@Execute`: executable method entrypoint
- `@Autocomplete`: autocomplete handler (method or parameter level)

### Parameter metadata

- `@Name`: explicit Discord option name
- `@Optional`: marks parameter optional
- `@Default`: fallback value when option absent
- parameter-level `@Autocomplete`: links autocomplete provider to option

### Cross-cutting policies

Composable at class or method level:

- `@GuildOnly`
- `@DmOnly`
- `@RequireUserPermissions`
- `@RequireBotPermissions`
- `@Check`
- `@Cooldown`

## Execution pipeline

1. Discord interaction is mapped into `CommandInteraction`.
2. `CommandFramework` resolves a `CommandDefinition` + `CommandHandler` from `CommandRegistry`.
3. `CommandContext` is created with a `CommandResponder`.
4. Checks, permission constraints, and cooldown rules are evaluated.
5. Parameters are bound via built-in and custom resolvers.
6. Handler method is invoked using cached reflection metadata.
7. `CommandResponse` outputs are translated by responder adapters.
8. Exceptions are delegated to `CommandExceptionHandler`.

## Extension points

- `ResolverRegistry`: custom typed option binding.
- `CheckRegistry`: reusable policy checks by ID.
- `AutocompleteRegistry`: autocomplete providers by ID.
- `CommandExceptionHandler`: global exception mapping/logging.

## Discord-specific behavior

- Supports slash command trees (root options, subcommands, groups).
- Supports user and message context commands.
- Autocomplete can come from annotation handlers or registered provider IDs.
- Follow-up response models exist in core; Discord follow-up helpers are intentionally explicit when unsupported.

## Recommended project structure

For larger bots, keep command code organized by concern:

- `commands/moderation/*`
- `commands/admin/*`
- `commands/utility/*`
- shared checks/resolvers in dedicated packages

This avoids large command classes and keeps policy rules reusable.
