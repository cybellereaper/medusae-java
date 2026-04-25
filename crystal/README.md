# Medusae Crystal Port

This directory contains the Crystal rewrite of Medusae.

## Migration phases completed

### Phase 1: Core runtime primitives
- `Medusae::Gateway::GatewayIntent`
- `Medusae::Client::DiscordClientConfig`
- `Medusae::Client::SlashCommandRouter`

### Phase 2: Payload and command model
- `Medusae::Client::AutocompleteChoice`
- `Medusae::Client::DiscordOAuthScopes`
- `Medusae::Client::DiscordPermissions`
- `Medusae::Client::DiscordEmbed`
- `Medusae::Client::DiscordMessage`
- `Medusae::Client::SlashCommandOptionDefinition`
- `Medusae::Client::SlashCommandDefinition`

## Run tests

```bash
cd crystal
crystal spec
```
