package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.util.Validator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

class SpentTime {

    private final Plugin plugin;
    private final Logger logger;

    SpentTime(@NotNull Plugin plugin) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.logger = plugin.getLogger();
    }

    void enable() {

    }

    void disable() {

    }
}
