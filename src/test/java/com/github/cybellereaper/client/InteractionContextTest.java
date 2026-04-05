package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class InteractionContextTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void readsNestedSlashCommandOptions() throws Exception {
        JsonNode interaction = MAPPER.readTree("""
                {
                  "id": "1",
                  "token": "abc",
                  "type": 2,
                  "data": {
                    "name": "admin",
                    "options": [
                      {
                        "name": "user",
                        "options": [
                          { "name": "target", "value": "alice" }
                        ]
                      }
                    ]
                  }
                }
                """);

        InteractionContext context = InteractionContext.from(interaction, (id, token, type, data) -> {
        });

        assertEquals("alice", context.optionString("target"));
        assertNull(context.optionString("missing"));
    }

    @Test
    void validatesAutocompleteChoiceLimit() throws Exception {
        JsonNode interaction = MAPPER.readTree("""
                {
                  "id": "1",
                  "token": "abc",
                  "type": 4,
                  "data": {
                    "name": "echo"
                  }
                }
                """);

        InteractionContext context = InteractionContext.from(interaction, (id, token, type, data) -> {
        });
        List<AutocompleteChoice> choices = java.util.stream.IntStream.range(0, 26)
                .mapToObj(i -> new AutocompleteChoice("choice-" + i, "value-" + i))
                .toList();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> context.respondWithAutocompleteChoices(choices));
        assertTrue(ex.getMessage().contains("at most 25"));
    }

    @Test
    void supportsDefersAndBasicMetadata() throws Exception {
        JsonNode interaction = MAPPER.readTree("""
                {
                  "id": "9",
                  "token": "xyz",
                  "type": 5,
                  "data": {
                    "custom_id": "feedback_modal"
                  }
                }
                """);

        AtomicReference<Integer> responseType = new AtomicReference<>();
        AtomicInteger responseCount = new AtomicInteger();
        InteractionContext context = InteractionContext.from(interaction, (id, token, type, data) -> {
            responseType.set(type);
            responseCount.incrementAndGet();
        });

        assertEquals("9", context.id());
        assertEquals("xyz", context.token());
        assertEquals("feedback_modal", context.customId());

        context.deferUpdate();
        assertEquals(6, responseType.get());
        assertEquals(1, responseCount.get());
    }
}
