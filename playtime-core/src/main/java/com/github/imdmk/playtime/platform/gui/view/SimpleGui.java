package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface SimpleGui extends IdentifiableGui {

    BaseGui createGui();

    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer);
}
