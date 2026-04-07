# Interaction Command Framework Migration Notes

This guide describes incremental migration from command-only handlers to module-based command + UI interaction flows.

## Compatibility

Existing `registerCommands(...)` usage remains valid and unchanged.

## Incremental migration path

1. Keep existing slash command handlers returning `String` / `ImmediateResponse`.
2. Adopt `InteractionReply` when richer payloads are needed.
3. Register command and UI handlers together via `registerModules(...)`.
4. Add `@ButtonHandler`, select handlers, and `@ModalHandler` for workflow-driven interactions.
5. Use `@PathParam` and `@Field` for typed custom-id route and modal field binding.

## Response model highlights

- `InteractionReply` supports content, embeds, components, ephemeral flags, defer, and update modes.
- `ModalReply` can open modals from command, component, or context handlers.
- `String` return values remain supported and map to immediate public responses.

## Stateful custom-id route strategy

Custom IDs can include an optional state segment after `|`, for example:

```text
ticket:close:42|signedStatePayload
```

The framework:

- extracts route params from the left segment
- passes the optional state payload through interaction execution context
- enables custom codecs/session resolvers to decode and validate route state
