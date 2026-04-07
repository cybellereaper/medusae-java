package com.github.cybellereaper.medusae.discord.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.medusae.discord.model.MessageCreateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonJsonCodecTest {
    @Test
    void roundTripsRecord() {
        JsonCodec codec = new JacksonJsonCodec(new ObjectMapper());
        String json = codec.write(new MessageCreateRequest("hello"));
        MessageCreateRequest request = codec.read(json, MessageCreateRequest.class);
        assertEquals("hello", request.content());
    }
}
