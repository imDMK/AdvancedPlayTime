package com.github.imdmk.spenttime.shared.config.catalog;

import com.github.imdmk.spenttime.shared.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a catalog (or registry) of root configuration sections
 * that should be created and managed by the configuration system.
 *
 * <p>This abstraction allows different plugin modules or features
 * to provide their own isolated sets of configuration roots, which
 * can then be merged or registered by a central {@code ConfigManager}.</p>
 *
 * <p><strong>Typical usage:</strong></p>
 * <pre>{@code
 * public final class CoreConfigCatalog implements ConfigCatalog {
 *     @Override
 *     public @NotNull List<Class<? extends ConfigSection>> rootSections() {
 *         return List.of(PluginConfig.class, MessageConfig.class);
 *     }
 * }
 *
 * configManager.createAll(new CoreConfigCatalog().rootSections());
 * }</pre>
 *
 * <p><strong>Contract:</strong></p>
 * <ul>
 *   <li>Returned classes must extend {@link ConfigSection}.</li>
 *   <li>Each class must represent a distinct root YAML file.</li>
 *   <li>The list may be static or dynamically composed at runtime.</li>
 * </ul>
 *
 * @see ConfigSection
 * @see com.github.imdmk.spenttime.shared.config.ConfigManager
 */
public interface ConfigCatalog {

    /**
     * Returns a list of all root {@link ConfigSection} classes
     * provided by this catalog.
     *
     * @return non-null immutable list of configuration section classes
     */
    @NotNull List<Class<? extends ConfigSection>> rootSections();
}
