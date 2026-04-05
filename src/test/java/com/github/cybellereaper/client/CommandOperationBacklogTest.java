package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandOperationBacklogTest {
    @Test
    void storesFailedOperationAndRetriesItOnFlush() throws Exception {
        CommandOperationBacklog backlog = new CommandOperationBacklog();
        AtomicInteger attempts = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> backlog.execute("global:sync", () -> {
            if (attempts.getAndIncrement() == 0) {
                throw new RuntimeException("offline");
            }
            return JsonNodeFactory.instance.objectNode().put("ok", true);
        }));

        assertEquals(1, backlog.size());

        backlog.flush();

        assertEquals(2, attempts.get());
        assertEquals(0, backlog.size());
    }

    @Test
    void clearsBacklogWhenOperationEventuallySucceeds() throws Exception {
        CommandOperationBacklog backlog = new CommandOperationBacklog();

        JsonNode response = backlog.execute("global:create:ping", () -> JsonNodeFactory.instance.objectNode().put("id", "1"));

        assertEquals("1", response.path("id").asText());
        assertEquals(0, backlog.size());
    }

    @Test
    void keepsOnlyLatestPendingOperationForSameKey() {
        CommandOperationBacklog backlog = new CommandOperationBacklog();
        AtomicInteger first = new AtomicInteger(0);
        AtomicInteger second = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> backlog.execute("guild:sync:123", () -> {
            first.incrementAndGet();
            throw new RuntimeException("offline");
        }));

        assertThrows(RuntimeException.class, () -> backlog.execute("guild:sync:123", () -> {
            second.incrementAndGet();
            throw new RuntimeException("offline");
        }));

        backlog.flush();

        assertEquals(1, first.get());
        assertEquals(2, second.get());
        assertEquals(1, backlog.size());
    }
}
