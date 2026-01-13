package com.github.imdmk.playtime.platform.placeholder;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderRegistry {

    void register(@NotNull PluginPlaceholder placeholder);

    void unregister(@NotNull PluginPlaceholder placeholder);

    void unregisterAll();

}
