package com.github.imdmk.playtime.core.platform.placeholder;

final class DisabledPlaceholderRegistry implements PlaceholderRegistry {

    @Override
    public void register(PluginPlaceholder placeholder) {}

    @Override
    public void unregister(PluginPlaceholder placeholder) {}

    @Override
    public void unregisterAll() {}
}
