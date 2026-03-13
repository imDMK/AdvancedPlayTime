package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.config.ConfigSection;
import com.github.imdmk.playtime.core.config.ConfigService;
import com.github.imdmk.playtime.core.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
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

        configService.create(config.getClass());
        resources.on(config.getClass())
                .assignInstance(instance);

        for (Field field : config.getClass().getFields()) {
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
