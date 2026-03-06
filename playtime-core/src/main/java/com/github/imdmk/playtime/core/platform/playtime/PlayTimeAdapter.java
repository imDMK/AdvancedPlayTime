package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import org.bukkit.entity.Player;

public interface PlayTimeAdapter {

    PlayTime read(Player player);

    void write(Player player, PlayTime playTime);

}

