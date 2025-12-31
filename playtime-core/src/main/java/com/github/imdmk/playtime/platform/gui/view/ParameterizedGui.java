package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ParameterizedGui<T> extends IdentifiableGui {

    BaseGui createGui(@NotNull Player viewer, @NotNull T parameter);

    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter);

}
