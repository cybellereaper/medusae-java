package com.github.cybellereaper.medusae.discord.serialization;

public interface JsonCodec {
    String write(Object value);
    <T> T read(String json, Class<T> type);
}
