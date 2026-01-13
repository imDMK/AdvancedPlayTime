package com.github.imdmk.playtime.feature.playtime.top.gui;

import com.github.imdmk.playtime.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.injector.annotations.Gui;
import com.github.imdmk.playtime.platform.adventure.AdventureFormatter;
import com.github.imdmk.playtime.platform.adventure.AdventurePlaceholders;
import com.github.imdmk.playtime.platform.gui.GuiType;
import com.github.imdmk.playtime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.platform.gui.factory.GuiFactory;
import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.platform.gui.item.ItemGuiTransformer;
import com.github.imdmk.playtime.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import com.github.imdmk.playtime.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.platform.gui.render.TriumphGuiRenderer;
import com.github.imdmk.playtime.platform.gui.view.AbstractGui;
import com.github.imdmk.playtime.platform.gui.view.ParameterizedGui;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.time.Durations;
import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;
import java.util.function.Consumer;

@Gui
public final class PlayTimeTopGui
        extends AbstractGui
        implements ParameterizedGui<List<PlayTimeUser>> {

    private static final String GUI_ID = "playtime-top";

    private static final GuiRenderer RENDERER = TriumphGuiRenderer.newRenderer();
    private static final RenderOptions RENDER_OPTIONS = RenderOptions.defaultHide();

    private final Server server;
    private final PlayTimeTopGuiConfig guiConfig;

    @Inject
    PlayTimeTopGui(
            @NotNull Server server,
            @NotNull PlayTimeTopGuiConfig guiConfig,
            @NotNull NavigationBarConfig config,
            @NotNull TaskScheduler taskScheduler
    ) {
        super(config, taskScheduler, RENDERER, RENDER_OPTIONS);
        this.server = server;
        this.guiConfig = guiConfig;
    }

    @Override
    public BaseGui createGui(@NotNull Player viewer, @NotNull List<PlayTimeUser> topUsers) {
        return GuiFactory.build(guiConfig, BaseGui::disableAllInteractions);
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull List<PlayTimeUser> topUsers) {
        if (guiConfig.fillBorder) {
            final GuiItem borderItem = ItemGuiTransformer.toGuiItem(guiConfig.borderItem);
            gui.getFiller().fillBorder(borderItem);
        }

        placeExit(gui, viewer, exit -> gui.close(viewer));

        if (guiConfig.type == GuiType.PAGINATED) {
            placeNext(gui, viewer);
            placePrevious(gui, viewer);
        }

        final RenderContext context = RenderContext.defaultContext(viewer);
        final ItemGui item = guiConfig.playerEntryItem;

        for (int i = 0; i < topUsers.size(); i++) {
            final int position = i + 1;

            final PlayTimeUser user = topUsers.get(i);
            final OfflinePlayer player = server.getOfflinePlayer(user.getUuid());

            final AdventurePlaceholders placeholders = createPlaceholders(player, user, position);
            final Consumer<InventoryClickEvent> clickHandler = (event -> event.setCancelled(true));

            final Consumer<BaseItemBuilder<?>> editor = (builder) -> {
                if (builder instanceof SkullBuilder skullBuilder) {
                    skullBuilder.owner(player);
                }

                builder.name(AdventureFormatter.format(item.name(), placeholders));
                builder.lore(AdventureFormatter.format(item.lore(), placeholders));
            };

            renderer.addItem(gui, item, context, renderOptions, clickHandler, editor);
        }
    }

    private AdventurePlaceholders createPlaceholders(OfflinePlayer offlinePlayer, PlayTimeUser user, int position) {
        return AdventurePlaceholders.builder()
                .with("{PLAYER_NAME}", offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName())
                .with("{PLAYER_POSITION}", position)
                .with("{PLAYER_PLAYTIME}", Durations.format(user.getPlayTime().toDuration()))
                .build();
    }

    @Override
    public String getId() {
        return GUI_ID;
    }
}
