package com.github.imdmk.playtime;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayTimePluginLoader extends JavaPlugin {

    private PlayTimePlugin pluginCore;

    @Override
    public void onEnable() {
        final Plugin plugin = this;
        this.pluginCore = new PlayTimePlugin(plugin);
    }

    @Override
    public void onDisable() {
        if (this.pluginCore != null) {
            this.pluginCore.disable();
            this.pluginCore = null;
        }
    }
}
