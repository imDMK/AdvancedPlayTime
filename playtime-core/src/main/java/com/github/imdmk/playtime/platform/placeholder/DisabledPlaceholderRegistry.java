package com.github.imdmk.playtime.platform.placeholder;

import org.jetbrains.annotations.NotNull;

final class DisabledPlaceholderRegistry implements PlaceholderRegistry {

    @Override
    public void register(@NotNull PluginPlaceholder placeholder) {}

    @Override
    public void unregister(@NotNull PluginPlaceholder placeholder) {}

    @Override
    public void unregisterAll() {}
}
