package com.github.imdmk.playtime.core.platform.gui.config;

import com.github.imdmk.playtime.core.config.ConfigSection;
import com.github.imdmk.playtime.core.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.core.platform.adventure.AdventureComponentSerializer;
import com.github.imdmk.playtime.core.platform.gui.item.ItemGuiSerializer;
import com.github.imdmk.playtime.core.platform.serdes.EnchantmentSerializer;
import com.github.imdmk.playtime.core.platform.serdes.SoundSerializer;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@ConfigFile
public final class GuiConfig extends ConfigSection {

    @Comment({"#", "# Navigation Bar", "#"})
    public NavigationBarConfig navigationBar = new NavigationBarConfig();

    @Override
    public OkaeriSerdesPack serdesPack() {
        return registry -> {
            registry.register(new AdventureComponentSerializer());
            registry.register(new ItemGuiSerializer());
            registry.register(new EnchantmentSerializer());
            registry.register(new SoundSerializer());
        };
    }

    @Override
    public String fileName() {
        return "gui.yml";
    }
}
