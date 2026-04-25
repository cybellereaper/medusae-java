# Medusae Crystal Port

This directory contains the Crystal rewrite of Medusae's core interaction and payload model.

## Implemented modules

- Gateway
  - `Medusae::Gateway::GatewayIntent`
- Client core
  - `Medusae::Client::DiscordClientConfig`
  - `Medusae::Client::SlashCommandRouter`
- Message and embed payloads
  - `Medusae::Client::DiscordMessage`
  - `Medusae::Client::DiscordEmbed`
- Components and modal payloads
  - `Medusae::Client::DiscordButton`
  - `Medusae::Client::DiscordActionRow`
  - `Medusae::Client::DiscordSelectOption`
  - `Medusae::Client::DiscordStringSelectMenu`
  - `Medusae::Client::DiscordUserSelectMenu`
  - `Medusae::Client::DiscordRoleSelectMenu`
  - `Medusae::Client::DiscordMentionableSelectMenu`
  - `Medusae::Client::DiscordChannelSelectMenu`
  - `Medusae::Client::DiscordTextInput`
  - `Medusae::Client::DiscordModal`

## Run tests

```bash
cd crystal
crystal spec
```
