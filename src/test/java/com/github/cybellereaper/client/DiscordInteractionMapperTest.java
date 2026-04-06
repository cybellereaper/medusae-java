package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.commands.discord.adapter.DiscordInteractionMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscordInteractionMapperTest {
    @Test
    void rejectsNullInputs() {
        DiscordInteractionMapper mapper = new DiscordInteractionMapper();
        assertThrows(NullPointerException.class, () -> mapper.toCoreInteraction(null, null));
    }

    @Test
    void handlesMissingTargetIdWithoutNpe() throws Exception {
        DiscordInteractionMapper mapper = new DiscordInteractionMapper();
        var node = new ObjectMapper().readTree("""
                {
                  "id":"1",
                  "token":"t",
                  "type":2,
                  "data":{
                    "name":"inspect user",
                    "type":2
                  }
                }
                """);
        InteractionContext context = InteractionContext.from(node, (id, token, type, data) -> {});

        assertDoesNotThrow(() -> mapper.toCoreInteraction(node, context));
    }
}
