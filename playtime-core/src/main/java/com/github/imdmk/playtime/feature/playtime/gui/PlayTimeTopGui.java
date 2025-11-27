package com.github.imdmk.playtime.feature.playtime.gui;

import com.github.imdmk.playtime.platform.gui.config.GuiConfig;
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
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.adventure.AdventureFormatter;
import com.github.imdmk.playtime.shared.adventure.AdventurePlaceholders;
import com.github.imdmk.playtime.shared.gui.GuiType;
import com.github.imdmk.playtime.shared.time.Durations;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import com.github.imdmk.playtime.user.UserTime;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;
import java.util.function.Consumer;

public final class PlayTimeTopGui extends AbstractGui implements ParameterizedGui<List<User>> {

    private static final String GUI_IDENTIFIER = "playtime-top";

    private static final GuiRenderer GUI_RENDERER = TriumphGuiRenderer.newRenderer();
    private static final RenderOptions RENDER_OPTIONS = RenderOptions.defaultHide();
    private static final ItemVariantResolver ITEM_VARIANT_RESOLVER = new ItemVariantPermissionResolver();

    private final Server server;
    private final GuiConfig guiConfig;
    private final PlayTimeTopGuiConfig playtimeTopGuiConfig;
    private final UserService userService;

    @Inject
    public PlayTimeTopGui(@NotNull Server server,
                          @NotNull GuiConfig guiConfig,
                          @NotNull NavigationBarConfig navigationBarConfig,
                          @NotNull PlayTimeTopGuiConfig playtimeTopGuiConfig,
                          @NotNull TaskScheduler taskScheduler,
                          @NotNull UserService userService) {
        super(navigationBarConfig, taskScheduler, GUI_RENDERER, RENDER_OPTIONS);
        this.server = Validator.notNull(server, "server cannot be null");
        this.guiConfig = Validator.notNull(guiConfig, "guiConfig cannot be null");
        this.playtimeTopGuiConfig = Validator.notNull(playtimeTopGuiConfig, "playtimeTopGuiConfig cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
    }

    @Override
    public @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull List<User> users) {
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(users, "users cannot be null");

        return GuiFactory.build(playtimeTopGuiConfig, BaseGui::disableAllInteractions);
    }

    @Override
    public void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull List<User> users) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(users, "users cannot be null");

        if (guiConfig.fillBorder) {
            final GuiItem border = ItemGuiTransformer.toGuiItem(guiConfig.borderItem);
            gui.getFiller().fillBorder(border);
        }

        placeExit(gui, viewer, e -> gui.close(viewer));

        if (playtimeTopGuiConfig.type == GuiType.PAGINATED) {
            placeNext(gui, viewer);
            placePrevious(gui, viewer);
        }

        final var context = RenderContext.defaultContext(viewer);
        final var item = resolveItemFor(viewer, context);

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final int position = i + 1;

            final var placeholders = createPlaceholders(user, position);

            final Consumer<InventoryClickEvent> onClick = (click) -> {
                if (click.getClick() != playtimeTopGuiConfig.resetClickType) {
                    return;
                }

                user.setPlaytime(UserTime.ZERO);
                userService.save(user, UserSaveReason.GUI_RESET_CLICK);
                gui.close(viewer);// TODO ADD MESSAGE HANDLING
            };

            final Consumer<ItemBuilder> editor = (builder) -> {
                builder.setSkullOwner(server.getOfflinePlayer(user.getUuid()));
                builder.name(AdventureFormatter.format(item.name(), placeholders));
                builder.lore(AdventureFormatter.format(item.lore(), placeholders));
            };

            renderer.addItem(gui, item, context, renderOptions, onClick, editor);
        }
    }

    private ItemGui resolveItemFor(Player viewer, RenderContext context) {
        final ItemGui adminItem = playtimeTopGuiConfig.playerEntryAdminItem;
        final ItemGui item = playtimeTopGuiConfig.playerEntryItem;

        return ITEM_VARIANT_RESOLVER.resolve(viewer, context, List.of(adminItem), item);
    }

    private AdventurePlaceholders createPlaceholders(User topUser, int position) {
        return AdventurePlaceholders.builder()
                .with("{PLAYER_NAME}", topUser.getName())
                .with("{PLAYER_POSITION}", position)
                .with("{PLAYER_PLAYTIME}", Durations.format(topUser.getPlaytime().toDuration()))
                .with("{CLICK_RESET}", playtimeTopGuiConfig.resetClickType.name())
                .build();
    }

    @Override
    public @NotNull String getId() {
        return GUI_IDENTIFIER;
    }
}
