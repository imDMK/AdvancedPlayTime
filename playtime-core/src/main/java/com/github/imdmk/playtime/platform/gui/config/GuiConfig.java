package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.feature.playtime.gui.PlayTimeTopGuiConfig;
import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.platform.serdes.ComponentSerializer;
import com.github.imdmk.playtime.platform.serdes.EnchantmentSerializer;
import com.github.imdmk.playtime.platform.serdes.ItemGuiSerializer;
import com.github.imdmk.playtime.platform.serdes.SoundSerializer;
import com.github.imdmk.playtime.shared.adventure.AdventureComponents;
import com.github.imdmk.playtime.shared.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public final class GuiConfig extends ConfigSection {

    @Comment({" ", "# Enable border around all GUIs", " "})
    public boolean fillBorder = true;

    @Comment({" ", "# Item used as the border around GUIs", " "})
    public ItemGui borderItem = ItemGui.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name(AdventureComponents.text(" "))
            .lore(Collections.emptyList())
            .build();

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
