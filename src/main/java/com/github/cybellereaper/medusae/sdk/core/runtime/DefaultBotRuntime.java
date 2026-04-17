package com.github.cybellereaper.medusae.sdk.core.runtime;

import java.util.List;

final class DefaultBotRuntime implements BotRuntime {

    private final BotRuntimeConfig config;
    private final List<SdkModule> modules;
    private volatile boolean running;

    DefaultBotRuntime(BotRuntimeConfig config, List<SdkModule> modules) {
        this.config = config;
        this.modules = modules;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        // Startup orchestration will be implemented in later phases.
        running = true;
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
    }

    @Override
    public synchronized void reload() {
        stop();
        start();
    }

    @Override
    public BotRuntimeConfig config() {
        return config;
    }

    List<SdkModule> modules() {
        return modules;
    }
}
