package com.github.cybellereaper.medusae.sdk.plugins.api;

import com.github.cybellereaper.medusae.sdk.core.runtime.BotRuntime;
import com.github.cybellereaper.medusae.sdk.core.services.ServiceRegistry;

public interface PluginContext {

    BotRuntime runtime();

    ServiceRegistry services();
}
