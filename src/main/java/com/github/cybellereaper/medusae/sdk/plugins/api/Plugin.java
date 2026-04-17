package com.github.cybellereaper.medusae.sdk.plugins.api;

public interface Plugin {

    PluginDescriptor descriptor();

    void onLoad(PluginContext context);

    void onEnable(PluginContext context);

    void onDisable(PluginContext context);

    void onReload(PluginContext context);
}
