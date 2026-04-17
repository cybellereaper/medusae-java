# Discord Bot SDK v1 System Design (Foundation Draft)

## 1) System Design Doc

### Goals and posture
The SDK is designed as a high-level, opinionated Java runtime that hides low-level Discord protocol details behind a cohesive runtime, builder-driven registration APIs, and middleware pipelines.

Primary priorities (ordered):
1. Simplicity
2. Performance
3. Scale
4. Extensibility

### Architectural style
- **Hybrid runtime model:** command/event/interaction abstractions over REST + Gateway clients.
- **Middleware-driven pipelines:** command, interaction, event, REST, automation, and plugin lifecycle.
- **Service registry over heavy DI:** explicit runtime-owned composition with typed service lookup.
- **Pluggable state model:** in-memory cache by default with persistence abstraction and MongoDB-first provider.
- **Resilient worker separation:** gateway worker(s), REST worker(s), automation worker(s).

### Runtime lifecycle
Startup phases are represented explicitly to prevent drift:
1. validate config
2. initialize service registry
3. initialize storage/migrations
4. initialize cache
5. load plugins
6. register commands/events/interactions
7. initialize gateway/rest
8. initialize automation
9. initialize observability
10. signal ready

### Reliability model
- Failure isolation in handler execution.
- Retry + backoff for transient operations.
- Idempotency + duplicate detection for interaction/event processing.
- Restart-safe workflows and scheduled jobs.

### Security model
- secure intent defaults
- env-var token sourcing
- permission guard layers (discord native + sdk guards + custom predicates)
- replay/duplicate interaction protection hooks

---

## 2) Package/Class Layout (Initial)

```text
com.github.cybellereaper.medusae.sdk
  core.runtime
    DiscordSdk
    BotBuilder
    BotRuntime
    DefaultBotRuntime
    BotRuntimeConfig
    StartupPlan
    StartupPhase
    SdkModule
  core.services
    ServiceRegistry
  plugins.api
    Plugin
    PluginDescriptor
    PluginContext
```

This is a foundation scaffold that can grow into the larger internal segmented layout from the full spec while preserving one unified public artifact.

---

## 3) SDK API Draft

```java
BotRuntime runtime = DiscordSdk.builder()
    .tokenFromEnvironment("DISCORD_TOKEN")
    .automaticSharding(true)
    .shardCount(1)
    .module(myModule)
    .build();

runtime.start();
```

Draft API principles:
- Builder-first creation.
- Strict startup validation.
- High-level runtime entrypoint.
- Explicit extension points (`SdkModule`, plugins).

---

## 4) Plugin API Draft

```java
public interface Plugin {
  PluginDescriptor descriptor();
  void onLoad(PluginContext context);
  void onEnable(PluginContext context);
  void onDisable(PluginContext context);
  void onReload(PluginContext context);
}
```

Key design guarantees:
- versioned descriptor for compatibility checks
- explicit lifecycle hooks
- access to runtime + services through `PluginContext`

---

## 5) Code Scaffold Status

Implemented in this iteration:
- Runtime entrypoint and builder.
- Runtime config + validation.
- Ordered startup phase model.
- Core plugin lifecycle interfaces.
- Service registry contract.

Not yet implemented (next iterations):
- command/event/interaction pipelines
- middleware execution engine
- cache/storage providers
- automation scheduler/workers
- distributed/sharding orchestration

---

## 6) Example Bot (Draft)

```java
BotRuntime runtime = DiscordSdk.builder()
    .tokenFromEnvironment("DISCORD_TOKEN")
    .build();

runtime.start();
```

Next step is wiring command + interaction registration and sync support.

---

## 7) Roadmap

### Phase 1 (current)
- runtime bootstrap scaffold
- config validation
- startup phase contract
- plugin API contract

### Phase 2
- command framework + context objects
- interaction routers (buttons/selects/modals)
- middleware chain manager

### Phase 3
- cache manager + policies
- MongoDB provider + migrations
- automation durability + recovery

### Phase 4
- plugin manager + compatibility enforcement
- distributed coordination + sharding orchestration

### Phase 5
- CLI scaffolding + diagnostics
- test harness/mocks/simulation/snapshots
- docs generator + templates

### Phase 6
- API stabilization
- source compatibility hardening
- perf and observability polish
