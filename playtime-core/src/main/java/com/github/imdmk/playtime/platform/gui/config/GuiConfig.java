package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.feature.playtime.gui.PlayTimeTopGuiConfig;
import com.github.imdmk.playtime.platform.serdes.ComponentSerializer;
import com.github.imdmk.playtime.platform.serdes.EnchantmentSerializer;
import com.github.imdmk.playtime.platform.serdes.ItemGuiSerializer;
import com.github.imdmk.playtime.platform.serdes.SoundSerializer;
import com.github.imdmk.playtime.shared.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public final class GuiConfig extends ConfigSection {

    @Comment({"#", "# Playtime top GUI", "#"})
    public PlayTimeTopGuiConfig playtimeTopGui = new PlayTimeTopGuiConfig();

    @Comment({"#", "# Navigation Bar", "#"})
    public NavigationBarConfig navigationBar = new NavigationBarConfig();

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ComponentSerializer());
            registry.register(new ItemGuiSerializer());
            registry.register(new EnchantmentSerializer());
            registry.register(new SoundSerializer());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "guiConfig.yml";
    }
}
