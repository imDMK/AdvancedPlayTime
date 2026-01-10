package com.github.imdmk.playtime.feature.playtime.gui;

import com.github.imdmk.playtime.injector.annotations.Gui;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.adventure.AdventureFormatter;
import com.github.imdmk.playtime.platform.adventure.AdventurePlaceholders;
import com.github.imdmk.playtime.platform.gui.GuiType;
import com.github.imdmk.playtime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.platform.gui.factory.GuiFactory;
import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.platform.gui.item.ItemGuiTransformer;
import com.github.imdmk.playtime.platform.gui.item.ItemVariantPermissionResolver;
import com.github.imdmk.playtime.platform.gui.item.ItemVariantResolver;
import com.github.imdmk.playtime.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import com.github.imdmk.playtime.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.platform.gui.render.TriumphGuiRenderer;
import com.github.imdmk.playtime.platform.gui.view.AbstractGui;
import com.github.imdmk.playtime.platform.gui.view.ParameterizedGui;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.time.Durations;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import com.github.imdmk.playtime.user.UserTime;
import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
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
        implements ParameterizedGui<List<User>> {

    private static final String GUI_IDENTIFIER = "playtime-top";

    private static final GuiRenderer GUI_RENDERER = TriumphGuiRenderer.newRenderer();
    private static final RenderOptions RENDER_OPTIONS = RenderOptions.defaultHide();
    private static final ItemVariantResolver ITEM_VARIANT_RESOLVER = new ItemVariantPermissionResolver();

    private final Server server;
    private final PlayTimeTopGuiConfig topGuiConfig;
    private final MessageService messageService;
    private final UserService userService;

    @Inject
    public PlayTimeTopGui(
            @NotNull Server server,
            @NotNull NavigationBarConfig navigationBarConfig,
            @NotNull PlayTimeTopGuiConfig topGuiConfig,
            @NotNull TaskScheduler taskScheduler,
            @NotNull MessageService messageService,
            @NotNull UserService userService
    ) {
        super(navigationBarConfig, taskScheduler, GUI_RENDERER, RENDER_OPTIONS);
        this.server = server;
        this.topGuiConfig = topGuiConfig;
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull List<User> users) {
        return GuiFactory.build(topGuiConfig, BaseGui::disableAllInteractions);
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull List<User> users) {
        if (topGuiConfig.fillBorder) {
            final GuiItem borderItem = ItemGuiTransformer.toGuiItem(topGuiConfig.borderItem);
            gui.getFiller().fillBorder(borderItem);
        }

        placeExit(gui, viewer, e -> gui.close(viewer));

        if (topGuiConfig.type == GuiType.PAGINATED) {
            placeNext(gui, viewer);
            placePrevious(gui, viewer);
        }

        final RenderContext context = RenderContext.defaultContext(viewer);
        final ItemGui item = resolveItemFor(viewer, context);

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final int position = i + 1;

            final AdventurePlaceholders placeholders = createPlaceholders(user, position);

            final Consumer<InventoryClickEvent> onClick = (click) -> {
                if (click.getClick() != topGuiConfig.resetClickType) {
                    return;
                }

                gui.close(viewer);

                user.setPlaytime(UserTime.ZERO);
                userService.save(user, UserSaveReason.GUI_RESET_CLICK)
                        .thenAccept(result -> messageService.send(viewer, n -> n.playtimeMessages.playerPlaytimeReset()))
                        .exceptionally(e -> {
                            messageService.send(viewer, n -> n.actionExecutionError);
                            return null;
                        });
            };

            final Consumer<BaseItemBuilder<?>> editor = (builder) -> {
                if (builder instanceof SkullBuilder skullBuilder) {
                    skullBuilder.owner(server.getOfflinePlayer(user.getUuid()));
                }

                builder.name(AdventureFormatter.format(item.name(), placeholders));
                builder.lore(AdventureFormatter.format(item.lore(), placeholders));
            };

            renderer.addItem(gui, item, context, renderOptions, onClick, editor);
        }
    }

    private ItemGui resolveItemFor(Player viewer, RenderContext context) {
        final ItemGui adminItem = topGuiConfig.playerEntryAdminItem;
        final ItemGui item = topGuiConfig.playerEntryItem;
        return ITEM_VARIANT_RESOLVER.resolve(viewer, context, List.of(adminItem), item);
    }

    private AdventurePlaceholders createPlaceholders(User topUser, int position) {
        return AdventurePlaceholders.builder()
                .with("{PLAYER_NAME}", topUser.getName())
                .with("{PLAYER_POSITION}", position)
                .with("{PLAYER_PLAYTIME}", Durations.format(topUser.getPlaytime().toDuration()))
                .with("{CLICK_RESET}", topGuiConfig.resetClickType.name())
                .build();
    }

    @Override
    public @NotNull String getId() {
        return GUI_IDENTIFIER;
    }
}
