package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayTimeAdapter {

    PlayTime read(Player player);
    PlayTime read(UUID playerId);

    void write(Player player, PlayTime playTime);
    void write(UUID playerId, PlayTime playTime);

}

