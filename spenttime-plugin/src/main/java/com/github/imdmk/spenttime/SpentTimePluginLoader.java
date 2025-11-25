package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.shared.Validator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;


public final class SpentTimePluginLoader extends JavaPlugin {

    private final ExecutorService executor;
    private final PluginLoaderSettings settings;

    private volatile SpentTimePlugin core;

    public SpentTimePluginLoader(@NotNull ExecutorService executor, @NotNull PluginLoaderSettings settings) {
        this.executor = Validator.notNull(executor, "executor cannot be null");
        this.settings = Validator.notNull(settings, "settings cannot be null");
    }

    public SpentTimePluginLoader() {
        this(SpentTimeExecutorFactory.newWorkerExecutor(), new DefaultPluginLoaderSettings());
    }

    @Override
    public void onEnable() {
        this.core = new SpentTimePlugin(this, executor);
        this.core.enable(settings.configSections(), settings.pluginModules());
    }

    /**
     * Called by Bukkit when the plugin is being disabled, either on server shutdown
     * or via manual reload.
     */
    @Override
    public void onDisable() {
        if (this.core != null) {
            this.core.disable();
            this.core = null;
        }

        SpentTimeExecutorFactory.shutdownQuietly(executor);
    }
}
