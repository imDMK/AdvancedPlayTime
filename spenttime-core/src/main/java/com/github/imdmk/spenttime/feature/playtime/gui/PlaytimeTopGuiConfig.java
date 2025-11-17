package com.github.imdmk.spenttime.feature.playtime.gui;

import com.github.imdmk.spenttime.platform.gui.config.ConfigurableGui;
import com.github.imdmk.spenttime.platform.gui.item.ItemGui;
import com.github.imdmk.spenttime.shared.adventure.AdventureComponents;
import com.github.imdmk.spenttime.shared.gui.GuiType;
import eu.okaeri.configs.OkaeriConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

public class PlaytimeTopGuiConfig extends OkaeriConfig implements ConfigurableGui {

    public Component title = AdventureComponents.text("<yellow>Playtime top of server");

    public GuiType type = GuiType.SCROLLING_HORIZONTAL;

    public int rows = 6;

    public ItemGui headItem = ItemGui.builder()
            .material(Material.PLAYER_HEAD)
            .name(AdventureComponents.withoutItalics("<red>{PLAYER_POSITION}. <gray>Player <red>{PLAYER_NAME}"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<green>The player has spent <red>{PLAYER_PLAYTIME} <green>on the server<dark_gray>.",
                    " "
            ))
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    public ItemGui headItemAdmin = ItemGui.builder()
            .material(Material.PLAYER_HEAD)
            .name(AdventureComponents.withoutItalics("<red>{PLAYER_POSITION}. <gray>Player <red>{PLAYER_NAME}"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<green>The player has spent <red>{PLAYER_PLAYTIME} <green>on the server<dark_gray>.",
                    " ",
                    "<gray>Click <red>{CLICK_RESET} <gray>to <red>reset {PLAYER} <gray>spent time."
            ))
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .requiredPermission("spentime.user.manage")
            .build();

    public ClickType resetClick = ClickType.SHIFT_RIGHT;

    @Override
    public @NotNull Component title() {
        return title;
    }

    @Override
    public @NotNull GuiType type() {
        return type;
    }

    @Override
    public int rows() {
        return rows;
    }

}
