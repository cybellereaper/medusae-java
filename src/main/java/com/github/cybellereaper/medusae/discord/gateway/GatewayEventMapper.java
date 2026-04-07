package com.github.cybellereaper.medusae.discord.gateway;

import com.github.cybellereaper.medusae.discord.events.DiscordEvent;
import com.github.cybellereaper.medusae.discord.events.MessageCreateEvent;
import com.github.cybellereaper.medusae.discord.events.ReadyEvent;
import com.github.cybellereaper.medusae.discord.events.UnknownDispatchEvent;
import com.github.cybellereaper.medusae.discord.model.DiscordMessage;
import com.github.cybellereaper.medusae.discord.serialization.JsonCodec;

public final class GatewayEventMapper {
    private final JsonCodec codec;

    public GatewayEventMapper(JsonCodec codec) {
        this.codec = codec;
    }

    public DiscordEvent map(String eventType, String dataJson) {
        return switch (eventType) {
            case "READY" -> {
                ReadyData data = codec.read(dataJson, ReadyData.class);
                yield new ReadyEvent(data.sessionId(), data.resumeGatewayUrl());
            }
            case "MESSAGE_CREATE" -> new MessageCreateEvent(codec.read(dataJson, DiscordMessage.class));
            default -> new UnknownDispatchEvent(eventType, dataJson);
        };
    }

    private record ReadyData(String sessionId, String resumeGatewayUrl) {
    }
}
