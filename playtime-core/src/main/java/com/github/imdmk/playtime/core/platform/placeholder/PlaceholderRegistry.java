package com.github.imdmk.playtime.core.platform.placeholder;

public interface PlaceholderRegistry {

    void register(PluginPlaceholder placeholder);

    void unregister(PluginPlaceholder placeholder);

    void unregisterAll();

}
