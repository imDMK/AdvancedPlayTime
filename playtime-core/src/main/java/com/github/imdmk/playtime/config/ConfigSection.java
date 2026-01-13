package com.github.imdmk.playtime.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

public abstract class ConfigSection extends OkaeriConfig {

    public abstract OkaeriSerdesPack serdesPack();

    public abstract String fileName();

}
