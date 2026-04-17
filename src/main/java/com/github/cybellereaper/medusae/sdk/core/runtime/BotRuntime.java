package com.github.cybellereaper.medusae.sdk.core.runtime;

public interface BotRuntime {

    void start();

    void stop();

    void reload();

    BotRuntimeConfig config();
}
