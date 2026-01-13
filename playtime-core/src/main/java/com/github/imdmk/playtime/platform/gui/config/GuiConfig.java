package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.platform.adventure.AdventureComponentSerializer;
import com.github.imdmk.playtime.platform.gui.item.ItemGuiSerializer;
import com.github.imdmk.playtime.platform.serdes.EnchantmentSerializer;
import com.github.imdmk.playtime.platform.serdes.SoundSerializer;
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
