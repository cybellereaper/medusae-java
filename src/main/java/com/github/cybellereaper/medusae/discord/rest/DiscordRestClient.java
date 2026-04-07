package com.github.cybellereaper.medusae.discord.rest;

import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.errors.ApiException;
import com.github.cybellereaper.medusae.discord.errors.TransportException;
import com.github.cybellereaper.medusae.discord.model.DiscordMessage;
import com.github.cybellereaper.medusae.discord.model.GatewayBotInfo;
import com.github.cybellereaper.medusae.discord.model.MessageCreateRequest;
import com.github.cybellereaper.medusae.discord.serialization.JsonCodec;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public final class DiscordRestClient {
    private final HttpClient httpClient;
    private final DiscordClientConfig config;
    private final JsonCodec codec;
    private final RateLimiter rateLimiter;

    public DiscordRestClient(HttpClient httpClient, DiscordClientConfig config, JsonCodec codec) {
        this(httpClient, config, codec, new RateLimiter(Clock.systemUTC()));
    }

    DiscordRestClient(HttpClient httpClient, DiscordClientConfig config, JsonCodec codec, RateLimiter rateLimiter) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        this.config = Objects.requireNonNull(config, "config");
        this.codec = Objects.requireNonNull(codec, "codec");
        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter");
    }

    public DiscordMessage sendMessage(String channelId, MessageCreateRequest request) {
        String body = codec.write(request);
        HttpResponse<String> response = execute("POST", "/channels/" + channelId + "/messages", body);
        return codec.read(response.body(), DiscordMessage.class);
    }

    public GatewayBotInfo getGatewayBotInfo() {
        HttpResponse<String> response = execute("GET", "/gateway/bot", null);
        return codec.read(response.body(), GatewayBotInfo.class);
    }

    private HttpResponse<String> execute(String method, String path, String body) {
        String routeKey = method + " " + path;
        rateLimiter.acquire(routeKey);

        HttpRequest.Builder builder = HttpRequest.newBuilder(config.restUri(path))
                .timeout(config.requestTimeout())
                .header("Authorization", config.token().asAuthorizationHeader())
                .header("Accept", "application/json")
                .header("User-Agent", "medusae-discord/0.2");

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }

        try {
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            updateRateLimit(routeKey, response);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(response.statusCode(), response.body());
            }
            return response;
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new TransportException("REST execution failed", exception);
        }
    }

    private void updateRateLimit(String routeKey, HttpResponse<String> response) {
        var headers = response.headers();
        int remaining = headers.firstValue("X-RateLimit-Remaining").map(Integer::parseInt).orElse(Integer.MAX_VALUE);
        long resetAfterMs = headers.firstValue("X-RateLimit-Reset-After")
                .map(v -> (long) (Double.parseDouble(v) * 1000L))
                .orElse(0L);
        if (remaining == Integer.MAX_VALUE) {
            return;
        }
        rateLimiter.update(routeKey, remaining, Instant.now().plusMillis(Math.max(0L, resetAfterMs)));

        if (response.statusCode() == 429) {
            var retryAfter = headers.firstValue("Retry-After").orElse("0");
            rateLimiter.update(routeKey, 0, Instant.now().plusMillis((long) (Double.parseDouble(retryAfter) * 1000L)));
        }
    }

    public Map<String, Object> createInteractionResponsePayload(int type, Map<String, Object> data) {
        return data == null || data.isEmpty() ? Map.of("type", type) : Map.of("type", type, "data", data);
    }
}
