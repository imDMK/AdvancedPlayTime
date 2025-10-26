package com.github.imdmk.spenttime.shared.config;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class PluginConfig extends ConfigSection {

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return null;
    }

    @Override
    public @NotNull String getFileName() {
        return "";
    }
}
