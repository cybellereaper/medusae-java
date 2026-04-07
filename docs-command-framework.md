# Annotation Command Framework

This guide covers the annotation-first framework in `com.github.cybellereaper.commands`.

Use it when you need command modules that scale with typed options, reusable checks/resolvers, and explicit permission/cooldown policy.

## Architecture overview

The framework is split into two layers:

- **Core (transport-agnostic):** annotation parsing, metadata validation, command execution pipeline.
- **Discord adapter:** maps Discord interactions into core models and applies framework responses back to Discord.

This separation keeps command/business logic isolated from Discord transport concerns.

## Quick start

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

### Command declarations

- `@Command`: root slash/context command
- `@Subcommand`: slash subcommand handler
- `@SubcommandGroup`: groups related subcommands
- `@Execute`: executable entrypoint
- `@Autocomplete`: autocomplete handler (method or parameter level)

### Parameter metadata

- `@Name`: explicit Discord option name
- `@Optional`: parameter is optional
- `Optional<T>`: optional option while preserving option type
- `@Default`: fallback value when option is absent
- Parameter-level `@Autocomplete`: binds an option to a provider

### Cross-cutting policies

Composable at class or method scope:

- `@GuildOnly`
- `@DmOnly`
- `@RequireUserPermissions`
- `@RequireBotPermissions`
- `@Check`
- `@Cooldown`

## Execution pipeline

1. Map Discord interaction into `CommandInteraction`.
2. Resolve `CommandDefinition` + `CommandHandler` from `CommandRegistry`.
3. Create `CommandContext` with a `CommandResponder`.
4. Evaluate checks, permissions, and cooldown rules.
5. Bind parameters using built-in/custom resolvers.
6. Invoke handler using cached reflection metadata.
7. Translate `CommandResponse` through responder adapters.
8. Delegate exceptions to `CommandExceptionHandler`.

## Extension points

- `ResolverRegistry`: register custom typed option binding
- `CheckRegistry`: reusable policy checks by ID
- `AutocompleteRegistry`: autocomplete providers by ID
- `CommandExceptionHandler`: centralized exception mapping/logging

## Discord-specific behavior

- Full slash command trees (root options, subcommands, groups)
- User and message context commands
- Autocomplete from annotation handlers or registered provider IDs
- Core follow-up models supported; Discord follow-up helpers are explicit when unsupported

## Recommended project structure

For larger bots, organize commands by concern:

- `commands/moderation/*`
- `commands/admin/*`
- `commands/utility/*`
- Shared checks/resolvers in dedicated packages

This avoids large command classes and keeps policy logic reusable.
