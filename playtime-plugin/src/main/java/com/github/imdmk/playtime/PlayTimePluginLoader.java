package com.github.imdmk.playtime;

import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;


public final class PlayTimePluginLoader extends JavaPlugin {

    private final ExecutorService executor;
    private final PluginLoaderSettings settings;

    private volatile PlayTimePlugin pluginCore;

    public PlayTimePluginLoader(@NotNull ExecutorService executor, @NotNull PluginLoaderSettings settings) {
        this.executor = Validator.notNull(executor, "executor cannot be null");
        this.settings = Validator.notNull(settings, "settings cannot be null");
    }

    public PlayTimePluginLoader() {
        this(PlayTimeExecutorFactory.newWorkerExecutor(), new PluginLoaderDefaultSettings());
    }

    /**
     * Called by Bukkit when the plugin is being enabled
     */
    @Override
    public void onEnable() {
        final Plugin plugin = this;

        this.pluginCore = new PlayTimePlugin(plugin, executor);
        this.pluginCore.enable(settings.configSections(), settings.pluginModules());
    }

    /**
     * Called by Bukkit when the plugin is being disabled, either on server shutdown
     * or via manual reload.
     */
    @Override
    public void onDisable() {
        if (this.pluginCore != null) {
            this.pluginCore.disable();
            this.pluginCore = null;
        }

        PlayTimeExecutorFactory.shutdownQuietly(executor);
    }
}
