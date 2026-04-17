package com.github.cybellereaper.medusae.sdk.core.services;

public interface ServiceRegistry {

    <T> void register(Class<T> type, T instance);

    <T> T resolve(Class<T> type);
}
