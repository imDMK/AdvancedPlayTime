package com.github.imdmk.playtime.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PlayTimePlugin extends JavaPlugin {

    private PlayTimeCoreWrapper wrapper;

    @Override
    public void onEnable() {
        PlayTimeDependencyLoader dependencyLoader = new PlayTimeDependencyLoader();
        dependencyLoader.load(this);

        wrapper = PlayTimeCoreWrapper.create(getClass().getClassLoader());
        wrapper.enable(this);
    }

    @Override
    public void onDisable() {
        if (wrapper != null) {
            wrapper.disable();
        }
    }
}
