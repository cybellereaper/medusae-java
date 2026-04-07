package com.github.cybellereaper.medusae.discord.gateway;

public record GatewayPayload(int op, Long s, String t, String dJson) {
}
