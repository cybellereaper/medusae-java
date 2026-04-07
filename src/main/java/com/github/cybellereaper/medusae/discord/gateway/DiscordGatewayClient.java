package com.github.cybellereaper.medusae.discord.gateway;

import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.errors.GatewayException;
import com.github.cybellereaper.medusae.discord.events.EventDispatcher;
import com.github.cybellereaper.medusae.discord.internal.GatewayEnvelope;
import com.github.cybellereaper.medusae.discord.serialization.JsonCodec;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class DiscordGatewayClient implements AutoCloseable {
    private static final Duration RECONNECT_DELAY = Duration.ofSeconds(2);

    private final DiscordClientConfig config;
    private final JsonCodec codec;
    private final GatewayTransportFactory transportFactory;
    private final EventDispatcher dispatcher;
    private final GatewayEventMapper mapper;
    private final ScheduledExecutorService scheduler;

    private final AtomicReference<GatewaySessionState> state = new AtomicReference<>(GatewaySessionState.DISCONNECTED);

    private volatile GatewayTransport transport;
    private volatile String sessionId;
    private volatile Long sequence;
    private volatile boolean shouldResume;
    private volatile ScheduledFuture<?> heartbeatTask;

    public DiscordGatewayClient(
            DiscordClientConfig config,
            JsonCodec codec,
            GatewayTransportFactory transportFactory,
            EventDispatcher dispatcher
    ) {
        this.config = Objects.requireNonNull(config, "config");
        this.codec = Objects.requireNonNull(codec, "codec");
        this.transportFactory = Objects.requireNonNull(transportFactory, "transportFactory");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.mapper = new GatewayEventMapper(codec);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public GatewaySessionState state() {
        return state.get();
    }

    public synchronized void connect() {
        if (state.get() == GatewaySessionState.SHUTDOWN) {
            throw new GatewayException("Gateway already shut down");
        }
        if (state.get() == GatewaySessionState.CONNECTED || state.get() == GatewaySessionState.CONNECTING) {
            return;
        }
        state.set(GatewaySessionState.CONNECTING);
        URI gatewayUri = config.gatewayUri();
        this.transport = transportFactory.create(gatewayUri, this::onText, this::onClosed, this::onError);
        transport.connect();
        state.set(GatewaySessionState.CONNECTED);
    }

    private void onText(String message) {
        GatewayEnvelope envelope = codec.read(message, GatewayEnvelope.class);
        if (envelope.sequence() != null) {
            sequence = envelope.sequence();
        }

        switch (envelope.op()) {
            case 10 -> startHeartbeat(readHeartbeatInterval(envelope.data()));
            case 0 -> handleDispatch(envelope.type(), codec.write(envelope.data()));
            case 7 -> reconnect(true);
            case 9 -> reconnect(false);
            case 11 -> { /* heartbeat ack */ }
            case 1 -> sendHeartbeat();
            default -> { }
        }
    }

    private long readHeartbeatInterval(Object data) {
        var map = codec.read(codec.write(data), java.util.Map.class);
        Object interval = map.get("heartbeat_interval");
        if (!(interval instanceof Number number)) {
            throw new GatewayException("HELLO payload missing heartbeat_interval");
        }
        return number.longValue();
    }

    private void handleDispatch(String type, String dataJson) {
        if ("READY".equals(type)) {
            var ready = codec.read(dataJson, java.util.Map.class);
            sessionId = String.valueOf(ready.get("session_id"));
            shouldResume = true;
        }
        dispatcher.dispatch(mapper.map(type, dataJson));
    }

    private void startHeartbeat(long intervalMs) {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }
        heartbeatTask = scheduler.scheduleAtFixedRate(this::sendHeartbeat, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        if (shouldResume && sessionId != null && sequence != null) {
            sendPayload(6, java.util.Map.of("token", config.token().value(), "session_id", sessionId, "seq", sequence));
            state.set(GatewaySessionState.RESUMING);
        } else {
            sendPayload(2, java.util.Map.of(
                    "token", config.token().value(),
                    "intents", config.intents(),
                    "properties", java.util.Map.of("os", System.getProperty("os.name"), "browser", "medusae", "device", "medusae")
            ));
        }
    }

    private void sendHeartbeat() {
        sendPayload(1, sequence);
    }

    private void sendPayload(int op, Object data) {
        transport.send(codec.write(java.util.Map.of("op", op, "d", data)));
    }

    private void onClosed() {
        if (state.get() == GatewaySessionState.SHUTDOWN) {
            return;
        }
        reconnect(shouldResume);
    }

    private void onError(Throwable throwable) {
        reconnect(shouldResume);
    }

    private synchronized void reconnect(boolean canResume) {
        if (state.get() == GatewaySessionState.SHUTDOWN) {
            return;
        }
        shouldResume = canResume;
        state.set(GatewaySessionState.DISCONNECTED);
        scheduler.schedule(this::connect, RECONNECT_DELAY.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void close() {
        state.set(GatewaySessionState.SHUTDOWN);
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }
        if (transport != null) {
            transport.close();
        }
        scheduler.shutdownNow();
    }
}
