# Medusae (formerly `jellycord`)

Medusae is a Java library for building Discord bots with **two complementary command models** that share the same gateway and REST core.

- **Interaction Router API** (`DiscordClient` + `InteractionContext`): lightweight, handler-first, minimal abstraction.
- **Annotation Command Framework** (`com.github.cybellereaper.commands`): scalable typed commands with reusable checks, cooldowns, and schema sync.

Start small with direct handlers, then migrate to annotations as your bot surface grows.

## Features

- Gateway lifecycle management and event subscription
- Slash commands, context menus, autocomplete, components, and modals
- High-level interaction response helpers
- REST convenience layer (`DiscordApi`)
- Retry/backoff controls and rate-limit observability hooks
- Optional in-memory state cache
- Attachment upload helpers
- Voice transport primitives for gateway/audio frame workflows

## Installation

```gradle
implementation 'com.github.cybellereaper:Medusae:1.0.0'
```

## Choose a framework

| Goal | Recommended API |
| --- | --- |
| Small bot, direct handlers, low ceremony | **Interaction Router API** |
| Typed modules, reusable policies, scalable command architecture | **Annotation Command Framework** |
| Fine-grained endpoint control | `DiscordApi` (works with either framework) |

## Quick start: Interaction Router API

```java
String token = System.getenv("DISCORD_BOT_TOKEN");

DiscordClientConfig config = DiscordClientConfig.builder(token)
        .intents(GatewayIntent.combine(GatewayIntent.GUILDS, GatewayIntent.GUILD_MESSAGES))
        .build();

try (DiscordClient client = DiscordClient.create(config)) {
    client.onSlashCommandContext("ping", ctx -> ctx.respondWithMessage("pong"));

    client.onSlashCommandContext("echo", ctx -> {
        String text = ctx.requiredOptionString("text");
        ctx.respondEphemeral("You said: " + text);
    });

    client.registerGlobalSlashCommand("ping", "Reply with pong");
    client.registerGlobalSlashCommand("echo", "Echo text");

    client.login();
    Thread.currentThread().join();
}
```

### Router highlights

- Register handlers with `on*Context(...)`
- Read typed options and send responses through `InteractionContext`
- Support slash commands, components, autocomplete, and modal submissions

## Quick start: Annotation Command Framework

```java
CommandFramework framework = new CommandFramework();

framework.registerCheck("guildonly", ctx -> !ctx.interaction().dm());
framework.registerAutocomplete("membersearch", (ctx, value) -> List.of("alice", "bob"));
framework.registerCommands(new UserCommands());

DiscordCommandSyncService sync = new DiscordCommandSyncService(framework);
sync.syncGlobal(discordClient);
```

### Annotation highlights

- Slash commands, subcommands, and subcommand groups
- User/message context commands
- Typed parameter binding with custom resolvers
- Declarative checks, permissions, cooldowns, and autocomplete
- Discord schema export + sync service

See [`docs-command-framework.md`](docs-command-framework.md) for full details.

## Annotated gateway events

You can register gateway listeners with annotations in the same module-centric style:

```java
AnnotatedGatewayEventBinder binder = new AnnotatedGatewayEventBinder();
binder.bind(discordClient, new ModerationEvents());
```

```java
@EventModule
public final class ModerationEvents {
    @OnGatewayEvent(value = "READY", payload = ReadyEvent.class)
    public void onReady(ReadyEvent event) {
        System.out.println("Session: " + event.sessionId());
    }

    @OnGatewayEvent(value = "MESSAGE_CREATE", payload = MessageCreateEvent.class)
    public void onMessage(MessageCreateEvent event, DiscordClient client) {
        if ("!ping".equals(event.content())) {
            client.api().sendMessage(event.channelId(), DiscordMessage.ofContent("pong"));
        }
    }
}
```

Handler signature rules:

- Exactly one payload parameter compatible with `payload()`
- Optional `DiscordClient` parameter for client access
- Handler methods cannot be private

## Core configuration examples

### Sharding

```java
DiscordClientConfig config = DiscordClientConfig.builder(token)
        .intents(GatewayIntent.combine(GatewayIntent.GUILDS, GatewayIntent.GUILD_MESSAGES))
        .shard(1, 4) // shardId=1 out of 4 total shards
        .build();
```

### OAuth scopes

```java
String scopes = DiscordOAuthScopes.join(
        DiscordOAuthScopes.BOT,
        DiscordOAuthScopes.APPLICATIONS_COMMANDS
);
```

### Reliability hooks

```java
RateLimitObserver observer = new RateLimitObserver() {
    @Override
    public void onRetryScheduled(String method, String path, int attempt, Duration backoff, String reason) {
        System.out.println("retry " + method + " " + path + " attempt=" + attempt + " cause=" + reason);
    }
};

DiscordClient client = DiscordClient.create(
        config,
        RetryPolicy.defaultPolicy(),
        observer,
        true // enable state cache
);
```

### REST convenience calls

```java
JsonNode currentUser = client.api().getCurrentUser();
JsonNode channel = client.api().getChannel("1234567890");
client.api().deleteMessage("1234567890", "9876543210");
```

### Attachment uploads

```java
client.sendMessageWithAttachments(
        "123",
        DiscordMessage.ofContent("upload"),
        List.of(DiscordAttachment.fromPath(Path.of("/tmp/demo.png")))
);
```

## Additional documentation

- API reference: [`API.md`](API.md)
- Annotation framework guide: [`docs-command-framework.md`](docs-command-framework.md)
- Migration notes: [`docs-command-interaction-migration.md`](docs-command-interaction-migration.md)
- Examples: `src/main/java/com/github/cybellereaper/examples/commands`

## Run tests

```bash
./gradlew test
```
