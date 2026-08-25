package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.config.ConfigSection;
import com.github.imdmk.playtime.core.config.ConfigService;
import com.github.imdmk.playtime.core.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import eu.okaeri.configs.OkaeriConfig;
import org.panda_lang.utilities.inject.Resources;

import java.lang.reflect.Field;

public final class ConfigFileProcessor implements ComponentProcessor<ConfigFile> {

    private final ConfigService configService;

    public ConfigFileProcessor(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public Class<ConfigFile> annotation() {
        return ConfigFile.class;
    }

    @Override
    public void process(
            Object instance,
            ConfigFile annotation,
            ComponentProcessorContext context
    ) {
        Resources resources = context.injector().getResources();
        ConfigSection config = requireInstance(
                instance,
                ConfigSection.class,
                ConfigFile.class
        );

        // The very same instance must be loaded and bound, otherwise every component
        // injecting this config would receive an unloaded copy holding only the defaults.
        configService.adopt(config);

        resources.on(config.getClass())
                .assignInstance(config);

        bindSections(config, resources);
    }

    private void bindSections(ConfigSection config, Resources resources) {
        for (Field field : config.getClass().getFields()) {
            if (!OkaeriConfig.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {
                Object value = field.get(config);
                if (value != null) {
                    resources.on(field.getType())
                            .assignInstance(value);
                }
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException(
                        "Failed to read config field: " + field.getName(),
                        exception
                );
            }
        }
    }
}
