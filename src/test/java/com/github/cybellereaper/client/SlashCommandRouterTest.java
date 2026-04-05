package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SlashCommandRouterTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void routesApplicationCommandInteractionsToRegisteredHandlers() throws Exception {
        AtomicInteger invocationCount = new AtomicInteger(0);
        AtomicReference<String> seenInteractionId = new AtomicReference<>();

        SlashCommandRouter router = new SlashCommandRouter((id, token, content) -> {
        });
        router.registerHandler("ping", interaction -> {
            invocationCount.incrementAndGet();
            seenInteractionId.set(interaction.path("id").asText());
        });

        JsonNode interaction = interactionPayload(2, "ping", "42", "token-value");
        router.handleInteraction(interaction);

        assertEquals(1, invocationCount.get());
        assertEquals("42", seenInteractionId.get());
    }

    @Test
    void ignoresUnknownOrUnsupportedInteractions() throws Exception {
        AtomicInteger invocationCount = new AtomicInteger(0);

        SlashCommandRouter router = new SlashCommandRouter((id, token, content) -> {
        });
        router.registerHandler("ping", ignored -> invocationCount.incrementAndGet());

        router.handleInteraction(interactionPayload(2, "other", "1", "token"));
        router.handleInteraction(interactionPayload(3, "ping", "2", "token"));

        assertEquals(0, invocationCount.get());
    }

    @Test
    void respondWithMessageUsesInteractionMetadata() throws Exception {
        AtomicReference<String> responseId = new AtomicReference<>();
        AtomicReference<String> responseToken = new AtomicReference<>();
        AtomicReference<String> responseMessage = new AtomicReference<>();

        SlashCommandRouter router = new SlashCommandRouter((id, token, content) -> {
            responseId.set(id);
            responseToken.set(token);
            responseMessage.set(content);
        });

        router.respondWithMessage(interactionPayload(2, "ping", "123", "abc"), "pong");

        assertEquals("123", responseId.get());
        assertEquals("abc", responseToken.get());
        assertEquals("pong", responseMessage.get());
    }

    @Test
    void rejectsDuplicateCommandRegistration() {
        SlashCommandRouter router = new SlashCommandRouter((id, token, content) -> {
        });

        router.registerHandler("ping", interaction -> {
        });

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> router.registerHandler("ping", interaction -> {
                })
        );

        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    void respondWithMessageRequiresIdAndToken() throws Exception {
        SlashCommandRouter router = new SlashCommandRouter((id, token, content) -> {
        });

        JsonNode missingId = interactionPayload(2, "ping", "", "token");
        JsonNode missingToken = interactionPayload(2, "ping", "id", "");

        assertThrows(IllegalArgumentException.class, () -> router.respondWithMessage(missingId, "pong"));
        assertThrows(IllegalArgumentException.class, () -> router.respondWithMessage(missingToken, "pong"));
    }

    private static JsonNode interactionPayload(int type, String name, String id, String token) throws Exception {
        String json = """
                {
                  "type": %d,
                  "id": "%s",
                  "token": "%s",
                  "data": {
                    "name": "%s"
                  }
                }
                """.formatted(type, id, token, name);
        return MAPPER.readTree(json);
    }
}
