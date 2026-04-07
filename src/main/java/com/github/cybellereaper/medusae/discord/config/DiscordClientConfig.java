package com.github.cybellereaper.medusae.discord.config;

import com.github.cybellereaper.medusae.discord.auth.BotToken;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public record DiscordClientConfig(
        BotToken token,
        URI restBaseUri,
        URI gatewayBaseUri,
        int apiVersion,
        int intents,
        Duration requestTimeout,
        int maxReconnectAttempts
) {
    public DiscordClientConfig {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(restBaseUri, "restBaseUri");
        Objects.requireNonNull(gatewayBaseUri, "gatewayBaseUri");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (apiVersion <= 0) throw new IllegalArgumentException("apiVersion must be positive");
        if (requestTimeout.isNegative() || requestTimeout.isZero()) {
            throw new IllegalArgumentException("requestTimeout must be positive");
        }
        if (maxReconnectAttempts < 0) throw new IllegalArgumentException("maxReconnectAttempts must be >= 0");
    }

    public static Builder builder(BotToken token) {
        return new Builder(token);
    }

    public URI restUri(String path) {
        return restBaseUri.resolve(path.startsWith("/") ? path.substring(1) : path);
    }

    public URI gatewayUri() {
        String sep = gatewayBaseUri.toString().contains("?") ? "&" : "?";
        return URI.create(gatewayBaseUri + sep + "v=" + apiVersion + "&encoding=json");
    }

    public static final class Builder {
        private final BotToken token;
        private URI restBaseUri = URI.create("https://discord.com/api/v10/");
        private URI gatewayBaseUri = URI.create("wss://gateway.discord.gg/");
        private int apiVersion = 10;
        private int intents;
        private Duration requestTimeout = Duration.ofSeconds(15);
        private int maxReconnectAttempts = 10;

        private Builder(BotToken token) {
            this.token = Objects.requireNonNull(token, "token");
        }

        public Builder restBaseUri(URI restBaseUri) { this.restBaseUri = restBaseUri; return this; }
        public Builder gatewayBaseUri(URI gatewayBaseUri) { this.gatewayBaseUri = gatewayBaseUri; return this; }
        public Builder apiVersion(int apiVersion) { this.apiVersion = apiVersion; return this; }
        public Builder intents(int intents) { this.intents = intents; return this; }
        public Builder requestTimeout(Duration requestTimeout) { this.requestTimeout = requestTimeout; return this; }
        public Builder maxReconnectAttempts(int maxReconnectAttempts) { this.maxReconnectAttempts = maxReconnectAttempts; return this; }

        public DiscordClientConfig build() {
            return new DiscordClientConfig(token, restBaseUri, gatewayBaseUri, apiVersion, intents, requestTimeout, maxReconnectAttempts);
        }
    }
}
