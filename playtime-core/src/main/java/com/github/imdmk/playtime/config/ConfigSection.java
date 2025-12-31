package com.github.imdmk.playtime.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigSection extends OkaeriConfig {

    public abstract @NotNull OkaeriSerdesPack serdesPack();

    public abstract @NotNull String fileName();

}
