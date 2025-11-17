package com.github.imdmk.spenttime.shared.config;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class PluginConfig extends ConfigSection {

    public int topLimit = 50;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "pluginConfig.yml";
    }
}
