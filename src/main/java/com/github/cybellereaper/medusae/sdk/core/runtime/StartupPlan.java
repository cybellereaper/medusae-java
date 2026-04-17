package com.github.cybellereaper.medusae.sdk.core.runtime;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable startup execution plan that enforces mandatory startup ordering.
 */
public final class StartupPlan {

    private final List<StartupPhase> phases;

    private StartupPlan(List<StartupPhase> phases) {
        this.phases = List.copyOf(phases);
    }

    public static StartupPlan defaultPlan() {
        return of(List.of(StartupPhase.values()));
    }

    public static StartupPlan of(List<StartupPhase> phases) {
        Objects.requireNonNull(phases, "phases cannot be null");
        if (phases.isEmpty()) {
            throw new IllegalArgumentException("phases cannot be empty");
        }

        List<StartupPhase> copy = new ArrayList<>(phases.size());
        Set<StartupPhase> seen = EnumSet.noneOf(StartupPhase.class);
        for (StartupPhase phase : phases) {
            if (phase == null) {
                throw new IllegalArgumentException("phase cannot be null");
            }
            if (!seen.add(phase)) {
                throw new IllegalArgumentException("duplicate phase: " + phase);
            }
            copy.add(phase);
        }

        ensureComplete(copy, seen);
        ensureOrder(copy);
        return new StartupPlan(copy);
    }

    public List<StartupPhase> phases() {
        return phases;
    }

    private static void ensureComplete(List<StartupPhase> phases, Set<StartupPhase> seen) {
        for (StartupPhase required : StartupPhase.values()) {
            if (!seen.contains(required)) {
                throw new IllegalArgumentException("missing phase: " + required + " in plan " + phases);
            }
        }
    }

    private static void ensureOrder(List<StartupPhase> phases) {
        StartupPhase[] ordered = StartupPhase.values();
        int index = 0;
        for (StartupPhase phase : phases) {
            while (index < ordered.length && ordered[index] != phase) {
                index++;
            }
            if (index >= ordered.length) {
                throw new IllegalArgumentException("phase out of order: " + phase + " in plan " + phases);
            }
            index++;
        }
    }
}
