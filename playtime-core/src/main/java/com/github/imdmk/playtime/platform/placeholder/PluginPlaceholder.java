package com.github.imdmk.playtime.platform.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PluginPlaceholder {

    String identifier();

    String request(@NotNull Player player, @NotNull String params);

}
