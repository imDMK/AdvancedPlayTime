package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import org.jetbrains.annotations.NotNull;

public interface PlaceholderAdapter {

    boolean isAvailable();

    void register(@NotNull PluginPlaceholder placeholder);

    void unregister(@NotNull PluginPlaceholder placeholder);

    void unregisterAll();
}

