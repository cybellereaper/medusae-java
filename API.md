# Jellycord API Reference (Core)

## `DiscordClient`

`DiscordClient` is the main entry point for creating a bot client.

### Core lifecycle

- `DiscordClient.create(DiscordClientConfig config)`
- `login()`
- `close()`

### Config highlights (`DiscordClientConfig.Builder`)

- `intents(int intents)`
- `shard(int shardId, int shardCount)` for gateway sharding.

### Event handling

- `on(String eventType, Consumer<JsonNode> listener)`
- `on(String eventType, Class<T> eventClass, Consumer<T> listener)`
- `off(...)`

### Command registration

- `registerGlobalSlashCommand(...)`
- `registerGuildSlashCommand(...)`
- `registerGlobalSlashCommands(List<SlashCommandDefinition>)`
- `registerGuildSlashCommands(String guildId, List<SlashCommandDefinition>)`

### Interaction responses

- `respondWithMessage(...)`
- `respondWithEmbeds(...)`
- `respondEphemeral(...)`
- `respondWithAutocompleteChoices(...)`
- `respondWithModal(JsonNode, DiscordModal)`
- `deferMessage(...)`
- `deferUpdate(...)`

- `getModalValue(JsonNode, String)` for modal submit field extraction.

### Message sending

- `sendMessage(...)`
- `sendMessageWithEmbeds(...)`

### REST helper access

- `api()` returns a `DiscordApi` instance for common direct REST operations.

## `DiscordApi`

`DiscordApi` provides convenience methods on top of the underlying REST client.

- `getCurrentApplication()`
- `getCurrentUser()`
- `getChannel(String channelId)`
- `getGuild(String guildId)`
- `deleteMessage(String channelId, String messageId)`
- `request(String method, String path, Map<String, Object> body)`

### Validation rules

- IDs and required string arguments must be non-blank.
- `request(..., path, ...)` requires `path` to start with `/`.

## `DiscordOAuthScopes`

Helpers for OAuth scope composition:

- `join(String... scopes)` for normalized, deduplicated scopes.
- `defaultBotScopes()` for `bot` + `applications.commands`.
