package com.github.imdmk.playtime.core.config;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;

import java.io.File;

final class ConfigConfigurer {

    void configure(
            ConfigSection config,
            File file,
            OkaeriSerdesPack... serdesPacks
    ) {
        final YamlSnakeYamlConfigurer configurer = new YamlSnakeYamlConfigurer(YamlFactory.create());

        config.withConfigurer(configurer, serdesPacks)
                .withSerdesPack(new SerdesCommons())
                .withBindFile(file)
                .withRemoveOrphans(true);
    }
}
