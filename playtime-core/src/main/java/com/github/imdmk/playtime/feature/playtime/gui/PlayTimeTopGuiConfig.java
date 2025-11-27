package com.github.imdmk.playtime.feature.playtime.gui;

import com.github.imdmk.playtime.platform.gui.config.ConfigurableGui;
import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.shared.adventure.AdventureComponents;
import com.github.imdmk.playtime.shared.gui.GuiType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

public final class PlayTimeTopGuiConfig extends OkaeriConfig implements ConfigurableGui {

    @Comment({
            "#",
            "# Title of the top playtime GUI shown in the inventory window.",
            "# Displayed to the player as the inventory name.",
            "#"
    })
    public Component title = AdventureComponents.text("<yellow>Top playtime players");

    @Comment({
            "#",
            "# GUI layout type used to render the top playtime list.",
            "# Recommended: SCROLLING_HORIZONTAL for paginated horizontal scrolling.",
            "#"
    })
    public GuiType type = GuiType.SCROLLING_HORIZONTAL;

    @Comment({
            "#",
            "# Amount of inventory rows used by the GUI.",
            "# Valid range for standard chest GUIs: 1–6.",
            "#"
    })
    public int rows = 6;

    @Comment({
            "#",
            "# Base item for a single player entry in the top playtime list (regular players).",
            "# Used for non-admin viewers.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_POSITION}  - numeric position on the leaderboard (1, 2, 3, ...).",
            "#   {PLAYER_NAME}      - player nickname.",
            "#   {PLAYER_PLAYTIME}  - formatted playtime (e.g. 5h 32m).",
            "#"
    })
    public ItemGui playerEntryItem = ItemGui.builder()
            .material(Material.PLAYER_HEAD)
            .name(AdventureComponents.withoutItalics(
                    "<dark_gray>• <yellow>#{PLAYER_POSITION} <gray>- <red>{PLAYER_NAME}"
            ))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<dark_gray>▸ <gray>Playtime: <red>{PLAYER_PLAYTIME}",
                    " "
            ))
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    @Comment({
            "#",
            "# Base item for a single player entry in the top playtime list (admin view).",
            "# Used when the viewer has management permission and can reset playtime.",
            "#",
            "# Placeholders:",
            "#   {PLAYER_POSITION}  - numeric position on the leaderboard (1, 2, 3, ...).",
            "#   {PLAYER_NAME}      - player nickname.",
            "#   {PLAYER_PLAYTIME}  - formatted playtime (e.g. 5h 32m).",
            "#   {CLICK_RESET}      - name/description of the click used to reset (e.g. SHIFT + Right Click).",
            "#",
            "# requiredPermission in item defines to see the admin version of the item.",
            "#"
    })
    public ItemGui playerEntryAdminItem = ItemGui.builder()
            .material(Material.PLAYER_HEAD)
            .name(AdventureComponents.withoutItalics(
                    "<dark_gray>• <yellow>#{PLAYER_POSITION} <gray>- <red>{PLAYER_NAME}"
            ))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<dark_gray>▸ <gray>Playtime: <red>{PLAYER_PLAYTIME}",
                    " ",
                    "<dark_gray>▸ <gray>Click <red>{CLICK_RESET} <gray>to reset <red>{PLAYER_NAME}<gray>'s playtime."
            ))
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .requiredPermission("playtime.user.manage")
            .build();

    @Comment({
            "#",
            "# Click type used to trigger playtime reset in the admin view.",
            "# Must match how {CLICK_RESET} is described in messages/lore.",
            "# Example: SHIFT_RIGHT, SHIFT_LEFT, RIGHT, LEFT, etc.",
            "#"
    })
    public ClickType resetClickType = ClickType.SHIFT_RIGHT;

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
