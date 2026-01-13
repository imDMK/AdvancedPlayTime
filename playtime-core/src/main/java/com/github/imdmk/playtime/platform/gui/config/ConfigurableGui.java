package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.platform.gui.GuiType;
import net.kyori.adventure.text.Component;

public interface ConfigurableGui {

    Component title();

    GuiType type();

    int rows();
}

