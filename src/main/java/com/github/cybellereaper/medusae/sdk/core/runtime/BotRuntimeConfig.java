package com.github.cybellereaper.medusae.sdk.core.runtime;

import java.time.Duration;
import java.util.Objects;

/**
 * Core runtime configuration with strict startup validation.
 */
public final class BotRuntimeConfig {

    private static final Duration DEFAULT_SHUTDOWN_TIMEOUT = Duration.ofSeconds(30);

    private final String botToken;
    private final int shardCount;
    private final boolean automaticSharding;
    private final Duration shutdownTimeout;
    private final StartupPlan startupPlan;

    private BotRuntimeConfig(Builder builder) {
        this.botToken = builder.botToken;
        this.shardCount = builder.shardCount;
        this.automaticSharding = builder.automaticSharding;
        this.shutdownTimeout = builder.shutdownTimeout;
        this.startupPlan = builder.startupPlan;
    }

    public String botToken() {
        return botToken;
    }

    public int shardCount() {
        return shardCount;
    }

    public boolean automaticSharding() {
        return automaticSharding;
    }

    public Duration shutdownTimeout() {
        return shutdownTimeout;
    }

    public StartupPlan startupPlan() {
        return startupPlan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String botToken;
        private int shardCount = 1;
        private boolean automaticSharding = true;
        private Duration shutdownTimeout = DEFAULT_SHUTDOWN_TIMEOUT;
        private StartupPlan startupPlan = StartupPlan.defaultPlan();

        public Builder token(String botToken) {
            this.botToken = botToken;
            return this;
        }

        public Builder tokenFromEnvironment(String variableName) {
            Objects.requireNonNull(variableName, "variableName cannot be null");
            this.botToken = System.getenv(variableName);
            return this;
        }

        public Builder shardCount(int shardCount) {
            this.shardCount = shardCount;
            return this;
        }

        public Builder automaticSharding(boolean automaticSharding) {
            this.automaticSharding = automaticSharding;
            return this;
        }

        public Builder shutdownTimeout(Duration shutdownTimeout) {
            this.shutdownTimeout = shutdownTimeout;
            return this;
        }

        public Builder startupPlan(StartupPlan startupPlan) {
            this.startupPlan = startupPlan;
            return this;
        }

        public BotRuntimeConfig build() {
            validate();
            return new BotRuntimeConfig(this);
        }

        private void validate() {
            if (botToken == null || botToken.isBlank()) {
                throw new IllegalArgumentException("Discord bot token is required");
            }
            if (shardCount < 1) {
                throw new IllegalArgumentException("shardCount must be >= 1");
            }
            if (shutdownTimeout == null || shutdownTimeout.isZero() || shutdownTimeout.isNegative()) {
                throw new IllegalArgumentException("shutdownTimeout must be positive");
            }
            if (startupPlan == null) {
                throw new IllegalArgumentException("startupPlan is required");
            }
        }
    }
}
