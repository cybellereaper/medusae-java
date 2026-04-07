package com.github.cybellereaper.medusae.discord.gateway;

import java.net.URI;
import java.util.function.Consumer;

public interface GatewayTransportFactory {
    GatewayTransport create(URI uri, Consumer<String> onText, Runnable onClosed, Consumer<Throwable> onError);
}
