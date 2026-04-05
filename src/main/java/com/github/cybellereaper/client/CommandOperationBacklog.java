package com.github.cybellereaper.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class CommandOperationBacklog {
    private final Map<String, PendingOperation> pendingOperations = new ConcurrentHashMap<>();

    JsonNode execute(String operationKey, Supplier<JsonNode> operation) {
        String key = requireKey(operationKey);
        Objects.requireNonNull(operation, "operation");
        try {
            JsonNode result = operation.get();
            pendingOperations.remove(key);
            return result;
        } catch (RuntimeException failure) {
            pendingOperations.put(key, PendingOperation.fromSupplier(operation));
            throw failure;
        }
    }

    void execute(String operationKey, Runnable operation) {
        String key = requireKey(operationKey);
        Objects.requireNonNull(operation, "operation");
        try {
            operation.run();
            pendingOperations.remove(key);
        } catch (RuntimeException failure) {
            pendingOperations.put(key, PendingOperation.fromRunnable(operation));
            throw failure;
        }
    }

    void flush() {
        for (Map.Entry<String, PendingOperation> entry : pendingOperations.entrySet()) {
            String key = entry.getKey();
            PendingOperation operation = entry.getValue();
            try {
                operation.run();
                pendingOperations.remove(key, operation);
            } catch (RuntimeException ignored) {
                // Keep operation in the backlog and try again on next READY event.
            }
        }
    }

    int size() {
        return pendingOperations.size();
    }

    private static String requireKey(String operationKey) {
        Objects.requireNonNull(operationKey, "operationKey");
        if (operationKey.isBlank()) {
            throw new IllegalArgumentException("operationKey must not be blank");
        }
        return operationKey;
    }

    private record PendingOperation(Runnable task) {
        private PendingOperation {
            Objects.requireNonNull(task, "task");
        }

        static PendingOperation fromSupplier(Supplier<JsonNode> supplier) {
            return new PendingOperation(supplier::get);
        }

        static PendingOperation fromRunnable(Runnable runnable) {
            return new PendingOperation(runnable);
        }

        void run() {
            task.run();
        }
    }
}
