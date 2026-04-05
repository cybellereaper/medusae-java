package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cybellereaper.http.DiscordRestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * High-level REST API helper for common Discord resources.
 */
public final class DiscordApi {
    private final DiscordRestClient restClient;

    DiscordApi(DiscordRestClient restClient) {
        this.restClient = Objects.requireNonNull(restClient, "restClient");
    }

    public JsonNode getCurrentApplication() {
        return restClient.getCurrentApplication();
    }

    public JsonNode getCurrentUser() {
        return restClient.request("GET", "/users/@me", null);
    }

    public JsonNode getChannel(String channelId) {
        requireNonBlank(channelId, "channelId");
        return restClient.request("GET", "/channels/" + channelId, null);
    }

    public JsonNode getGuild(String guildId) {
        requireNonBlank(guildId, "guildId");
        return restClient.request("GET", "/guilds/" + guildId, null);
    }

    public JsonNode deleteMessage(String channelId, String messageId) {
        requireNonBlank(channelId, "channelId");
        requireNonBlank(messageId, "messageId");
        return restClient.request("DELETE", "/channels/" + channelId + "/messages/" + messageId, null);
    }

    public JsonNode addReaction(String channelId, String messageId, String emoji) {
        return restClient.request("PUT", reactionBasePath(channelId, messageId, emoji) + "/@me", null);
    }

    public JsonNode removeOwnReaction(String channelId, String messageId, String emoji) {
        return restClient.request("DELETE", reactionBasePath(channelId, messageId, emoji) + "/@me", null);
    }

    public JsonNode removeUserReaction(String channelId, String messageId, String emoji, String userId) {
        requireNonBlank(userId, "userId");
        return restClient.request("DELETE", reactionBasePath(channelId, messageId, emoji) + "/" + userId, null);
    }

    public JsonNode getReactions(String channelId, String messageId, String emoji) {
        return restClient.request("GET", reactionBasePath(channelId, messageId, emoji), null);
    }

    public JsonNode clearReaction(String channelId, String messageId, String emoji) {
        return restClient.request("DELETE", reactionBasePath(channelId, messageId, emoji), null);
    }

    public JsonNode clearReactions(String channelId, String messageId) {
        requireNonBlank(channelId, "channelId");
        requireNonBlank(messageId, "messageId");
        return restClient.request("DELETE", "/channels/" + channelId + "/messages/" + messageId + "/reactions", null);
    }

    public JsonNode request(String method, String path, Map<String, Object> body) {
        requireNonBlank(method, "method");
        requireNonBlank(path, "path");
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("path must start with '/'");
        }
        return restClient.request(method, path, body);
    }

    private static String reactionBasePath(String channelId, String messageId, String emoji) {
        requireNonBlank(channelId, "channelId");
        requireNonBlank(messageId, "messageId");
        requireNonBlank(emoji, "emoji");

        return "/channels/" + channelId + "/messages/" + messageId + "/reactions/" + encodePathSegment(emoji);
    }

    private static String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static void requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName);
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
