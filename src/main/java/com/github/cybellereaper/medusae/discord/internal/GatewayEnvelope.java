package com.github.cybellereaper.medusae.discord.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GatewayEnvelope(
        @JsonProperty("op") int op,
        @JsonProperty("s") Long sequence,
        @JsonProperty("t") String type,
        @JsonProperty("d") Object data
) {
}
