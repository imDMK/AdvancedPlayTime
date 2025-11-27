package com.github.imdmk.playtime;

import com.github.imdmk.playtime.feature.migration.MigrationConfig;
import com.github.imdmk.playtime.feature.migration.MigrationModule;
import com.github.imdmk.playtime.feature.playtime.PlayTimeModule;
import com.github.imdmk.playtime.feature.reload.ReloadModule;
import com.github.imdmk.playtime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.playtime.infrastructure.module.Module;
import com.github.imdmk.playtime.platform.gui.GuiModule;
import com.github.imdmk.playtime.platform.gui.config.GuiConfig;
import com.github.imdmk.playtime.shared.config.ConfigSection;
import com.github.imdmk.playtime.shared.config.PluginConfig;
import com.github.imdmk.playtime.shared.message.MessageConfig;
import com.github.imdmk.playtime.user.UserModule;
import com.github.imdmk.playtime.user.top.TopUsersCacheConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default bootstrap settings for PlayTime: config sections and plugin modules.
 */
class LoaderDefaultSettings implements LoaderSettings {

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
    public @NotNull List<Class<? extends Module>> pluginModules() {
        return List.of(
                UserModule.class,
                PlayTimeModule.class,
                GuiModule.class,
                MigrationModule.class,
                ReloadModule.class
        );
    }
}
