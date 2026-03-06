package com.github.imdmk.playtime.core.platform.placeholder;

import org.bukkit.entity.Player;


public interface PluginPlaceholder {

    String identifier();

    String request(Player player, String params);

}
