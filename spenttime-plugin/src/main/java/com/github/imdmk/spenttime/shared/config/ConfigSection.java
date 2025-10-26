package com.github.imdmk.spenttime.shared.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Abstract base class for configuration sections.
 *
 * <p>
 * Extends {@link OkaeriConfig} to provide a reusable foundation for plugin
 * configuration sections. Subclasses are required to specify the
 * serialization/deserialization pack and the configuration file name.
 * </p>
 *
 * <p>
 * Supports automatic recursive loading of nested {@link ConfigSection}
 * subclasses declared as fields inside this class.
 * </p>
 */
public abstract class ConfigSection extends OkaeriConfig {

    /**
     * Returns the {@link OkaeriSerdesPack} instance used for serializing and deserializing
     * this configuration section.
     *
     * @return non-null serialization/deserialization pack
     */
    public abstract @NotNull OkaeriSerdesPack getSerdesPack();

    /**
     * Returns the filename (including extension) used to persist this configuration section.
     *
     * @return non-null configuration file name
     */
    public abstract @NotNull String getFileName();

    /**
     * Loads the configuration from disk, then processes any post-load logic.
     * Recursively loads any nested {@link ConfigSection} fields declared in this class.
     *
     * @return this configuration instance for chaining
     * @throws OkaeriException if an error occurs during loading
     */
    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();
        this.loadProcessedProperties();
        try {
            this.loadNestedConfigSections(this.getClass());
        }
        catch (IllegalAccessException e) {
            Logger.getLogger(this.getClass().getName())
                    .severe("Could not load nested config sections: " + e.getMessage());
        }
        return this;
    }

    /**
     * Recursively loads nested {@link ConfigSection} fields declared as inner classes
     * or fields of this config section.
     *
     * @param fromClass the class to inspect for nested config sections
     * @throws IllegalAccessException if reflection access fails
     */
    private void loadNestedConfigSections(Class<?> fromClass) throws IllegalAccessException {
        List<Class<? extends ConfigSection>> nestedClasses = new ArrayList<>();

        // Collect all inner classes that extend ConfigSection
        for (Class<?> declaredClass : fromClass.getDeclaredClasses()) {
            if (ConfigSection.class.isAssignableFrom(declaredClass)) {
                nestedClasses.add(declaredClass.asSubclass(ConfigSection.class));
            }
        }

        // For each field, if its type matches one of the nested ConfigSection classes,
        // invoke loading logic on that field's instance.
        for (Field field : fromClass.getDeclaredFields()) {
            for (Class<?> nestedClass : nestedClasses) {
                if (field.getType().equals(nestedClass)) {
                    field.setAccessible(true);
                    if (field.canAccess(this)) {
                        ConfigSection nestedConfig = (ConfigSection) field.get(this);
                        if (nestedConfig != null) {
                            nestedConfig.loadProcessedProperties();
                            nestedConfig.loadNestedConfigSections(nestedClass);
                        }
                    }
                }
            }
        }
    }

    /**
     * Hook method called after configuration loading is completed.
     * Override in subclasses to perform validation or further processing.
     */
    public void loadProcessedProperties() {
        // Default no-op implementation
    }
}
