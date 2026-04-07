package com.github.cybellereaper.medusae.discord.commands;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandRegistry {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public void register(String name, Command command) {
        commands.put(name, command);
    }

    public Optional<Command> find(String name) {
        return Optional.ofNullable(commands.get(name));
    }
}
