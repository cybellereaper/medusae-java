package com.github.cybellereaper.medusae.sdk.core.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BotBuilder {

    private final BotRuntimeConfig.Builder configBuilder = BotRuntimeConfig.builder();
    private final List<SdkModule> modules = new ArrayList<>();

    public BotBuilder token(String token) {
        configBuilder.token(token);
        return this;
    }

    public BotBuilder tokenFromEnvironment(String variableName) {
        configBuilder.tokenFromEnvironment(variableName);
        return this;
    }

    public BotBuilder automaticSharding(boolean automatic) {
        configBuilder.automaticSharding(automatic);
        return this;
    }

    public BotBuilder shardCount(int shardCount) {
        configBuilder.shardCount(shardCount);
        return this;
    }

    public BotBuilder startupPlan(StartupPlan startupPlan) {
        configBuilder.startupPlan(startupPlan);
        return this;
    }

    public BotBuilder module(SdkModule module) {
        modules.add(Objects.requireNonNull(module, "module cannot be null"));
        return this;
    }

    public BotRuntime build() {
        BotRuntimeConfig config = configBuilder.build();
        return new DefaultBotRuntime(config, List.copyOf(modules));
    }
}
