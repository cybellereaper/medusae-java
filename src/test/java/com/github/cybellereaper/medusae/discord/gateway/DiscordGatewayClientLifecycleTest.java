package com.github.cybellereaper.medusae.discord.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.medusae.discord.auth.BotToken;
import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.events.EventDispatcher;
import com.github.cybellereaper.medusae.discord.serialization.JacksonJsonCodec;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordGatewayClientLifecycleTest {
    @Test
    void reconnectsAfterCloseSignal() throws Exception {
        AtomicInteger connectCount = new AtomicInteger();
        FakeFactory factory = new FakeFactory(connectCount);
        DiscordClientConfig config = new DiscordClientConfig(
                new BotToken("test-token"),
                URI.create("https://discord.com/api/v10/"),
                URI.create("wss://gateway.discord.gg/"),
                10,
                0,
                java.time.Duration.ofSeconds(2),
                3
        );

        DiscordGatewayClient client = new DiscordGatewayClient(
                config,
                new JacksonJsonCodec(new ObjectMapper()),
                factory,
                new EventDispatcher(Runnable::run)
        );

        client.connect();
        factory.close();
        Thread.sleep(2300);

        assertEquals(2, connectCount.get());
        client.close();
    }

    private static final class FakeFactory implements GatewayTransportFactory {
        private final AtomicInteger connectCount;
        private Runnable onClosed;

        private FakeFactory(AtomicInteger connectCount) {
            this.connectCount = connectCount;
        }

        @Override
        public GatewayTransport create(URI uri, Consumer<String> onText, Runnable onClosed, Consumer<Throwable> onError) {
            this.onClosed = onClosed;
            return new GatewayTransport() {
                @Override
                public void connect() {
                    connectCount.incrementAndGet();
                }

                @Override
                public void send(String payload) {
                }

                @Override
                public void close() {
                }
            };
        }

        void close() {
            onClosed.run();
        }
    }
}
