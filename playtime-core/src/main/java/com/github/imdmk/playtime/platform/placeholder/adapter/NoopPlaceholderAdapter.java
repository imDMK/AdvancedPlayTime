package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import org.jetbrains.annotations.NotNull;

final class NoopPlaceholderAdapter implements PlaceholderAdapter {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void register(@NotNull PluginPlaceholder placeholder) {}

    @Override
    public void unregister(@NotNull PluginPlaceholder placeholder) {}

    @Override
    public void unregisterAll() {}
}
