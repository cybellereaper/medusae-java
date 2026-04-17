package com.github.cybellereaper.medusae.sdk.core.runtime;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartupPlanTest {

    @Test
    void shouldCreateDefaultPlanInSpecOrder() {
        StartupPlan plan = StartupPlan.defaultPlan();

        assertEquals(List.of(StartupPhase.values()), plan.phases());
    }

    @Test
    void shouldRejectPlanMissingPhase() {
        List<StartupPhase> missingReady = List.of(
                StartupPhase.VALIDATE_CONFIGURATION,
                StartupPhase.INITIALIZE_SERVICE_REGISTRY,
                StartupPhase.INITIALIZE_STORAGE,
                StartupPhase.INITIALIZE_CACHE,
                StartupPhase.LOAD_PLUGINS,
                StartupPhase.REGISTER_HANDLERS,
                StartupPhase.INITIALIZE_DISCORD_CLIENTS,
                StartupPhase.INITIALIZE_AUTOMATION,
                StartupPhase.INITIALIZE_OBSERVABILITY
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> StartupPlan.of(missingReady));

        assertTrue(ex.getMessage().contains("missing phase"));
    }

    @Test
    void shouldRejectOutOfOrderPlan() {
        List<StartupPhase> phases = List.of(
                StartupPhase.VALIDATE_CONFIGURATION,
                StartupPhase.INITIALIZE_SERVICE_REGISTRY,
                StartupPhase.INITIALIZE_STORAGE,
                StartupPhase.INITIALIZE_CACHE,
                StartupPhase.LOAD_PLUGINS,
                StartupPhase.REGISTER_HANDLERS,
                StartupPhase.INITIALIZE_AUTOMATION,
                StartupPhase.INITIALIZE_DISCORD_CLIENTS,
                StartupPhase.INITIALIZE_OBSERVABILITY,
                StartupPhase.SIGNAL_READY
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> StartupPlan.of(phases));

        assertTrue(ex.getMessage().contains("out of order"));
    }
}
