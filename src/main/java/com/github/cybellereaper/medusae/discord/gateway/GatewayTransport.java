package com.github.cybellereaper.medusae.discord.gateway;

public interface GatewayTransport extends AutoCloseable {
    void connect();
    void send(String payload);
    @Override
    void close();
}
