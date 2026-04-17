package com.github.cybellereaper.medusae.sdk.core.runtime;

import com.github.cybellereaper.medusae.sdk.core.services.ServiceRegistry;

public interface SdkModule {

    String name();

    default void configure(ServiceRegistry registry) {
        // Default no-op
    }
}
