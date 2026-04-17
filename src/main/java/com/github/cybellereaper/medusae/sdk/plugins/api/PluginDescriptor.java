package com.github.cybellereaper.medusae.sdk.plugins.api;

import java.util.List;

public record PluginDescriptor(
        String id,
        String version,
        String sdkVersionRange,
        List<String> dependencies
) {
}
