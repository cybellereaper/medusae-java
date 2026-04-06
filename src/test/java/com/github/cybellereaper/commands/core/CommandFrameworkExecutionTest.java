package com.github.cybellereaper.commands.core;

import com.github.cybellereaper.commands.core.annotation.*;
import com.github.cybellereaper.commands.core.exception.RegistrationException;
import com.github.cybellereaper.commands.core.execute.CommandFramework;
import com.github.cybellereaper.commands.core.model.CommandInteraction;
import com.github.cybellereaper.commands.core.model.CommandOptionValue;
import com.github.cybellereaper.commands.core.model.CommandType;
import com.github.cybellereaper.commands.core.response.ImmediateResponse;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CommandFrameworkExecutionTest {

    @Test
    void executesSlashCommandWithTypedResolution() {
        CommandFramework framework = new CommandFramework();
        var handler = new GreetingCommand();
        framework.registerCommands(handler);

        AtomicReference<Object> capturedResponse = new AtomicReference<>();
        framework.execute(new CommandInteraction(
                "greet", CommandType.CHAT_INPUT, null, null,
                Map.of("name", new CommandOptionValue("Cybele", 3)),
                null, Map.of(), true, null, "u1", Set.of(), Set.of(),
                null, null, null, null, null, null
        ), response -> capturedResponse.set(response));

        assertEquals("Cybele", handler.lastName);
        assertInstanceOf(ImmediateResponse.class, capturedResponse.get());
    }

    @Test
    void failsFastWhenCheckIsMissing() {
        CommandFramework framework = new CommandFramework();
        assertThrows(RegistrationException.class, () -> framework.registerCommands(new CheckedCommand()));
    }

    @Test
    void executesCustomCheck() {
        CommandFramework framework = new CommandFramework();
        framework.registerCheck("allow", ctx -> true);
        framework.registerCommands(new CheckedCommand());

        assertDoesNotThrow(() -> framework.execute(new CommandInteraction(
                "checked", CommandType.CHAT_INPUT, null, null,
                Map.of(), null, Map.of(), true, null, "user", Set.of(), Set.of(),
                null, null, null, null, null, null
        ), response -> {}));
    }

    @Test
    void dispatchesAutocompleteProvider() {
        CommandFramework framework = new CommandFramework();
        framework.registerAutocomplete("reasons", (ctx, value) -> List.of(value + "1", value + "2"));
        framework.registerCommands(new AutoCommand());

        List<String> values = framework.executeAutocomplete(new CommandInteraction(
                "auto", CommandType.CHAT_INPUT, null, null,
                Map.of("reason", new CommandOptionValue("sp", 3)),
                "reason", Map.of(), true, null, "u", Set.of(), Set.of(),
                null, null, null, null, null, null
        ), response -> {});

        assertEquals(List.of("sp1", "sp2"), values);
    }

    @Command("greet")
    static final class GreetingCommand {
        private String lastName;

        @Execute
        ImmediateResponse root(@Name("name") String name) {
            lastName = name;
            return ImmediateResponse.publicMessage("hello");
        }
    }

    @Command("checked")
    @Check("allow")
    static final class CheckedCommand {
        @Execute
        void root() {}
    }

    @Command("auto")
    static final class AutoCommand {
        @Execute
        void root(@Name("reason") @Autocomplete("reasons") String reason) {}
    }
}
