package com.github.cybellereaper.medusae.sdk.core.runtime;

/**
 * Ordered bootstrap phases for runtime startup.
 */
public enum StartupPhase {
    VALIDATE_CONFIGURATION,
    INITIALIZE_SERVICE_REGISTRY,
    INITIALIZE_STORAGE,
    INITIALIZE_CACHE,
    LOAD_PLUGINS,
    REGISTER_HANDLERS,
    INITIALIZE_DISCORD_CLIENTS,
    INITIALIZE_AUTOMATION,
    INITIALIZE_OBSERVABILITY,
    SIGNAL_READY
}
