package com.github.imdmk.spenttime.shared.config.catalog;

import com.github.imdmk.spenttime.feature.migration.MigrationConfig;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.spenttime.platform.gui.config.GuiConfig;
import com.github.imdmk.spenttime.shared.config.ConfigSection;
import com.github.imdmk.spenttime.shared.config.PluginConfig;
import com.github.imdmk.spenttime.shared.message.MessageConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default implementation of {@link ConfigCatalog} defining the
 * primary configuration roots for the SpentTime plugin.
 *
 * <p>This catalog lists all essential {@link ConfigSection}s that are expected
 * to exist in a typical plugin environment — including global plugin settings,
 * database configuration, messages, and GUI layout files.</p>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 * ConfigManager configManager = new ConfigManager(dataFolder);
 * configManager.createAll(new DefaultConfigCatalog().rootSections());
 * }</pre>
 *
 * <p><strong>Contract:</strong> Each class in {@link #rootSections()} must extend
 * {@link ConfigSection} and represent a separate root YAML file.</p>
 *
 * @see com.github.imdmk.spenttime.shared.config.ConfigManager
 */
public class DefaultConfigCatalog implements ConfigCatalog {

    /**
     * Returns the default set of root configuration sections used by the plugin.
     *
     * <p>These sections cover global plugin settings, messages, database connections,
     * and GUI layout configurations.</p>
     *
     * @return immutable list of root configuration classes
     */
    @Override
    public @NotNull List<Class<? extends ConfigSection>> rootSections() {
        return List.of(
                PluginConfig.class,
                MessageConfig.class,
                DatabaseConfig.class,
                GuiConfig.class,
                MigrationConfig.class
        );
    }
}
