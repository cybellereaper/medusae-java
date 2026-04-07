package com.github.cybellereaper.medusae.discord.gateway;

import com.github.cybellereaper.medusae.discord.errors.GatewayException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public final class JdkGatewayTransportFactory implements GatewayTransportFactory {
    private final HttpClient httpClient;

    public JdkGatewayTransportFactory(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public GatewayTransport create(URI uri, Consumer<String> onText, Runnable onClosed, Consumer<Throwable> onError) {
        return new GatewayTransport() {
            private volatile WebSocket webSocket;

            @Override
            public void connect() {
                this.webSocket = httpClient.newWebSocketBuilder().buildAsync(uri, new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        if (last) {
                            onText.accept(data.toString());
                        }
                        webSocket.request(1);
                        return null;
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        onClosed.run();
                        return null;
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        onError.accept(error);
                    }
                }).join();
                this.webSocket.request(1);
            }

            @Override
            public void send(String payload) {
                WebSocket socket = webSocket;
                if (socket == null) {
                    throw new GatewayException("Transport not connected");
                }
                socket.sendText(payload, true).join();
            }

            @Override
            public void close() {
                if (webSocket != null) {
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown").join();
                }
            }
        };
    }
}
