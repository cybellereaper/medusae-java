package com.github.cybellereaper.medusae.discord.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class EventDispatcher {
    private final Map<Class<? extends DiscordEvent>, List<Consumer<? extends DiscordEvent>>> listeners = new ConcurrentHashMap<>();
    private final Executor executor;

    public EventDispatcher(Executor executor) {
        this.executor = executor;
    }

    public <T extends DiscordEvent> void on(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, ignored -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends DiscordEvent> void dispatch(T event) {
        List<Consumer<? extends DiscordEvent>> typed = listeners.getOrDefault(event.getClass(), List.of());
        for (Consumer<? extends DiscordEvent> consumer : typed) {
            executor.execute(() -> ((Consumer<T>) consumer).accept(event));
        }
    }
}
