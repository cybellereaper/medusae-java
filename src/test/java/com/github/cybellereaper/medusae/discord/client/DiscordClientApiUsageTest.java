package com.github.cybellereaper.medusae.discord.client;

import com.github.cybellereaper.medusae.discord.api.DiscordClient;
import com.github.cybellereaper.medusae.discord.auth.BotToken;
import com.github.cybellereaper.medusae.discord.config.DiscordClientConfig;
import com.github.cybellereaper.medusae.discord.events.MessageCreateEvent;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscordClientApiUsageTest {
    @Test
    void allowsRegisteringEventListeners() throws InterruptedException {
        DiscordClientConfig config = new DiscordClientConfig(
                new BotToken("token"),
                URI.create("https://discord.com/api/v10/"),
                URI.create("wss://gateway.discord.gg/"),
                10,
                0,
                java.time.Duration.ofSeconds(1),
                1
        );

        GatewayTransportFactoryStub factory = new GatewayTransportFactoryStub();
        DiscordClient client = DiscordClientBuilder.create(config)
                .gatewayTransportFactory(factory)
                .build();

        CountDownLatch invoked = new CountDownLatch(1);
        client.onEvent(MessageCreateEvent.class, evt -> invoked.countDown());
        client.connect();

        factory.emit("{\"op\":0,\"t\":\"MESSAGE_CREATE\",\"d\":{\"id\":\"1\",\"channelId\":\"2\",\"content\":\"hello\",\"author\":{\"id\":\"3\",\"username\":\"u\",\"discriminator\":\"0001\"}}}");

        assertTrue(invoked.await(1, TimeUnit.SECONDS));
        client.close();
    }

    private static final class GatewayTransportFactoryStub implements com.github.cybellereaper.medusae.discord.gateway.GatewayTransportFactory {
        private Consumer<String> onText = ignored -> {};

        @Override
        public com.github.cybellereaper.medusae.discord.gateway.GatewayTransport create(URI uri, Consumer<String> onText, Runnable onClosed, Consumer<Throwable> onError) {
            this.onText = onText;
            return new com.github.cybellereaper.medusae.discord.gateway.GatewayTransport() {
                @Override
                public void connect() {
                }

                @Override
                public void send(String payload) {
                }

                @Override
                public void close() {
                }
            };
        }

        void emit(String payload) {
            onText.accept(payload);
        }
    }
}
