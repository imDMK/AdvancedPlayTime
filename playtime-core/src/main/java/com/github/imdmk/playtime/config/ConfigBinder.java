package com.github.imdmk.playtime.config;

import com.github.imdmk.playtime.shared.validate.Validator;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;

import java.io.File;

final class ConfigBinder {

    void bind(@NotNull ConfigSection config, @NotNull File file) {
        Validator.notNull(config, "config");
        Validator.notNull(file, "file");

        final OkaeriSerdesPack serdesPack = config.getSerdesPack();
        final YamlSnakeYamlConfigurer yamlConfigurer = YamlConfigurerFactory.create();

        config.withConfigurer(yamlConfigurer)
                .withSerdesPack(serdesPack)
                .withSerdesPack(new SerdesCommons())
                .withBindFile(file)
                .withRemoveOrphans(true);
    }
}
