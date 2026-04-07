package com.github.cybellereaper.medusae.discord.events;

import com.github.cybellereaper.medusae.discord.model.DiscordMessage;
import com.github.cybellereaper.medusae.discord.model.DiscordUser;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventDispatcherTest {
    @Test
    void dispatchesTypedEventToListener() {
        EventDispatcher dispatcher = new EventDispatcher(Runnable::run);
        AtomicReference<String> captured = new AtomicReference<>();
        dispatcher.on(MessageCreateEvent.class, event -> captured.set(event.message().content()));

        dispatcher.dispatch(new MessageCreateEvent(new DiscordMessage("1", "2", "hi", new DiscordUser("3", "bot", "0001"))));

        assertEquals("hi", captured.get());
    }
}
