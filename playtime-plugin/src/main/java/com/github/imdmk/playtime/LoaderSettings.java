package com.github.imdmk.playtime;

import com.github.imdmk.playtime.infrastructure.module.PluginModule;
import com.github.imdmk.playtime.shared.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Defines the bootstrap configuration used by {@link PlayTimePluginLoader}.
 *
 * <p>This interface decouples the loader from concrete configuration sources,
 * enabling custom setups (testing, profiling, modular distributions, etc.).</p>
 */
public interface LoaderSettings {

    /**
     * Returns a list of all {@link ConfigSection} types that should be registered
     * and loaded during plugin bootstrap.
     *
     * @return non-null list of configuration section classes
     */
    @NotNull List<Class<? extends ConfigSection>> configSections();

    /**
     * Returns the ordered list of {@link PluginModule} classes that define
     * the plugin's functional modules (features, services, listeners, commands).
     *
     * @return non-null list of plugin module classes
     */
    @NotNull List<Class<? extends PluginModule>> pluginModules();
}
