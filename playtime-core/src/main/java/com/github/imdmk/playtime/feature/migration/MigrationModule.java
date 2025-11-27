package com.github.imdmk.playtime.feature.migration;

import com.github.imdmk.playtime.feature.migration.migrator.PlayerMigrator;
import com.github.imdmk.playtime.feature.migration.migrator.RepositoryPlayerMigrator;
import com.github.imdmk.playtime.feature.migration.provider.BukkitPlayerProvider;
import com.github.imdmk.playtime.feature.migration.provider.PlayerProvider;
import com.github.imdmk.playtime.feature.migration.runner.BlockingMigrationRunner;
import com.github.imdmk.playtime.infrastructure.module.PluginModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public final class MigrationModule implements PluginModule {

    private PlayerMigrator migrator;
    private PlayerProvider provider;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(PlayerMigrator.class).assignInstance(() -> migrator);
        resources.on(PlayerProvider.class).assignInstance(() -> provider);
    }

    @Override
    public void init(@NotNull Injector injector) {}

    @Override
    public void afterRegister(@NotNull Plugin plugin, @NotNull Server server, @NotNull Injector injector) {
        this.migrator = injector.newInstance(RepositoryPlayerMigrator.class);
        this.provider = injector.newInstance(BukkitPlayerProvider.class);

        var blockingRunner = injector.newInstance(BlockingMigrationRunner.class);
        blockingRunner.execute();
    }

    @Override
    public int order() {
        return 10;
    }
}
