package com.github.imdmk.playtime.platform.playtime;

import com.github.imdmk.playtime.PlayTime;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayTimeAdapter {

    @NotNull PlayTime read(@NotNull Player player);

    void write(@NotNull Player player, @NotNull PlayTime playTime);

}

