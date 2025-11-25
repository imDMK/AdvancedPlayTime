package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.feature.migration.MigrationConfig;
import com.github.imdmk.spenttime.feature.migration.MigrationModule;
import com.github.imdmk.spenttime.feature.playtime.PlaytimeModule;
import com.github.imdmk.spenttime.feature.reload.ReloadModule;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.platform.gui.GuiModule;
import com.github.imdmk.spenttime.platform.gui.config.GuiConfig;
import com.github.imdmk.spenttime.shared.config.ConfigSection;
import com.github.imdmk.spenttime.shared.config.PluginConfig;
import com.github.imdmk.spenttime.shared.message.MessageConfig;
import com.github.imdmk.spenttime.user.UserModule;
import com.github.imdmk.spenttime.user.top.TopUsersCacheConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default bootstrap settings for SpentTime: config sections and plugin modules.
 */
class DefaultPluginLoaderSettings implements PluginLoaderSettings {

    @Override
    public @NotNull List<Class<? extends ConfigSection>> configSections() {
        return List.of(
                PluginConfig.class,
                MessageConfig.class,
                DatabaseConfig.class,
                GuiConfig.class,
                MigrationConfig.class,
                TopUsersCacheConfig.class
        );
    }

    @Override
    public @NotNull List<Class<? extends PluginModule>> pluginModules() {
        return List.of(
                UserModule.class,
                PlaytimeModule.class,
                GuiModule.class,
                MigrationModule.class,
                ReloadModule.class
        );
    }
}
