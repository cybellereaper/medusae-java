package com.github.cybellereaper.gateway.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageReactionAddEvent(
        @JsonProperty("user_id") String userId,
        @JsonProperty("channel_id") String channelId,
        @JsonProperty("message_id") String messageId,
        @JsonProperty("guild_id") String guildId,
        Emoji emoji
) {
    public record Emoji(
            String id,
            String name,
            boolean animated
    ) {
    }
}
