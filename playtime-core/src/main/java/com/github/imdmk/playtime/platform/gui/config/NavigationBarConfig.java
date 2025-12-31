package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.platform.adventure.AdventureComponents;
import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;

public final class NavigationBarConfig extends OkaeriConfig {

    @Comment({
            "# ",
            "# Item used to navigate to the next page in a paginated GUI.",
            "# Displayed only if a next page exists.",
            "# Usually placed on the right side of the navigation bar.",
            "# Recommended: simple directional item like ARROW.",
            "# ",
    })
    public ItemGui nextItem = ItemGui.builder()
            .material(Material.ARROW)
            .name(AdventureComponents.withoutItalics("<green>Next page"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<gray>Click <red>RIGHT <gray>to go to the next page",
                    " "
            ))
            .build();

    @Comment({
            "# ",
            "# Item shown when there is no next page available.",
            "# Used to indicate the end of the list or pagination limit.",
            "# Recommended: non-interactive item like BARRIER.",
            "# ",
    })
    public ItemGui noNextItem = ItemGui.builder()
            .material(Material.BARRIER)
            .name(AdventureComponents.withoutItalics("<red>There's no next page!"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<red>Sorry, there is no next page available.",
                    " "
            ))
            .build();

    @Comment({
            "# ",
            "# Item used to navigate to the previous page in a paginated GUI.",
            "# Displayed only if a previous page exists.",
            "# Usually placed on the left side of the navigation bar.",
            "# Recommended: ARROW or another clear directional icon.",
            "# ",
    })
    public ItemGui previousItem = ItemGui.builder()
            .material(Material.ARROW)
            .name(AdventureComponents.withoutItalics("<green>Previous page"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<gray>Click <red>LEFT <gray>to go to the previous page",
                    " "
            ))
            .build();

    @Comment({
            "# ",
            "# Item shown when there is no previous page available.",
            "# Used to indicate the start of the list or that the player cannot go back further.",
            "# Recommended: BARRIER or similar disabled-style item.",
            "# ",
    })
    public ItemGui noPreviousItem = ItemGui.builder()
            .material(Material.BARRIER)
            .name(AdventureComponents.withoutItalics("<red>There's no previous page!"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<red>Sorry, there is no previous page available.",
                    " "
            ))
            .build();

    @Comment({
            "# ",
            "# Item used to exit or close the current GUI.",
            "# Usually placed in the middle or bottom of the navigation bar.",
            "# Recommended: button or item that clearly indicates closure (e.g., ACACIA_BUTTON).",
            "# ",
    })
    public ItemGui exitItem = ItemGui.builder()
            .material(Material.ACACIA_BUTTON)
            .name(AdventureComponents.withoutItalics("<red>Exit GUI"))
            .lore(AdventureComponents.withoutItalics(
                    " ",
                    "<gray>Click <red>LEFT <gray>to exit this GUI",
                    " "
            ))
            .build();

}
