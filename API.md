# Medusae API Reference (Core)

This API surface backs both Medusae command styles:

- Interaction Router API
- Annotation Command Framework

## `DiscordClient`

`DiscordClient` is the primary runtime entry point.

### Lifecycle

- `DiscordClient.create(DiscordClientConfig config)`
- `login()`
- `close()`

### Configuration (`DiscordClientConfig.Builder`)

- `intents(int intents)`
- `shard(int shardId, int shardCount)`

### Event handling

- `on(String eventType, Consumer<JsonNode> listener)`
- `on(String eventType, Class<T> eventClass, Consumer<T> listener)`
- `off(...)`

### Command registration

- `registerGlobalSlashCommand(...)`
- `registerGuildSlashCommand(...)`
- `registerGlobalSlashCommands(List<SlashCommandDefinition>)`
- `registerGuildSlashCommands(String guildId, List<SlashCommandDefinition>)`

### Interaction handling

Prefer `on*Context(...)` handlers and respond via `InteractionContext`.

`InteractionContext` includes helpers for:

- **Responses**: `respondWithMessage`, `respondWithEmbeds`, `respondEphemeral`, `respondWithModal`, `respondWithAutocompleteChoices`, `deferMessage`, `deferUpdate`
- **Options**: `optionString`, `requiredOptionString`, `optionLong`, `optionInt`, `optionBoolean`, `optionDouble`
- **Resolved entities**: `resolvedAttachment`, `resolvedUser`, `resolvedMember`, `resolvedRole`, `resolvedChannel`
- **Typed resolved wrappers**: `resolvedAttachmentValue`, `resolvedUserValue`, `resolvedMemberValue`, `resolvedRoleValue`, `resolvedChannelValue`
- **Option-to-resolved helpers**: `optionResolvedAttachment`, `optionResolvedUser`, `optionResolvedRole`, `optionResolvedChannel`
- **Typed option-to-resolved wrappers**: `optionResolvedAttachmentValue`, `optionResolvedUserValue`, `optionResolvedRoleValue`, `optionResolvedChannelValue`
- **Modal fields**: `modalValue`
- **Interaction metadata**: `id`, `token`, `interactionType`, `commandType`, `guildId`, `channelId`, `userId`

> Legacy raw `JsonNode` interaction helpers remain available but are deprecated.

### Message sending

- `sendMessage(...)`
- `sendMessageWithEmbeds(...)`

### REST access

- `api()` returns `DiscordApi`

## `DiscordApi`

`DiscordApi` exposes convenience wrappers over the underlying REST client.

### Common methods

- `getCurrentApplication()`
- `getCurrentUser()`
- `getChannel(String channelId)`
- `getGuild(String guildId)`
- `deleteMessage(String channelId, String messageId)`
- `request(String method, String path, Map<String, Object> body)`

### Validation rules

- IDs and required string values must be non-blank.
- `request(..., path, ...)` requires `path` to begin with `/`.

## `DiscordOAuthScopes`

OAuth scope composition helpers:

- `join(String... scopes)` for normalized, deduplicated scopes
- `defaultBotScopes()` for `bot` + `applications.commands`
