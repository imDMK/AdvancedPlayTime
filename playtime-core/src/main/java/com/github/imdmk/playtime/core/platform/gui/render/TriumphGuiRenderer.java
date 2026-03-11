package com.github.imdmk.playtime.core.platform.gui.render;

import com.github.imdmk.playtime.core.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.core.platform.gui.item.ItemGuiTransformer;
import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

public final class TriumphGuiRenderer implements GuiRenderer {

    public static TriumphGuiRenderer newRenderer() {
        return new TriumphGuiRenderer();
    }

    @Override
    @Contract(mutates = "param1")
    public void setItem(
            BaseGui gui,
            int slot,
            ItemGui item,
            RenderContext context,
            RenderOptions options,
            Consumer<InventoryClickEvent> onClick,
            Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        GuiItem builtItem = buildGuiItem(
                item,
                context,
                options,
                onClick,
                builderEditor
        );
        if (builtItem != null) {
            gui.setItem(slot, builtItem);
        }
    }

    @Override
    @Contract(mutates = "param1")
    public void addItem(
            BaseGui gui,
            ItemGui item,
            RenderContext context,
            RenderOptions options,
            Consumer<InventoryClickEvent> onClick,
            Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        final GuiItem builtItem = buildGuiItem(
                item,
                context,
                options,
                onClick,
                builderEditor
        );
        if (builtItem != null) {
            gui.addItem(builtItem);
        }
    }

    private static GuiItem buildGuiItem(
            ItemGui item,
            RenderContext context,
            RenderOptions options,
            Consumer<InventoryClickEvent> onClick,
            Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        String requiredPerm = item.requiredPermission();

        boolean allowedForViewerNow = hasPermission(requiredPerm, context, context.viewer());
        if (!allowedForViewerNow && options.policy() == NoPermissionPolicy.HIDE) {
            return null;
        }

        GuiAction<InventoryClickEvent> clickHandler = event -> {
            if (!hasPermission(requiredPerm, context, event.getWhoClicked())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                options.onDenied().accept(event);
                return;
            }

            onClick.accept(event);
        };

        return ItemGuiTransformer.toGuiItem(item, clickHandler, builderEditor);
    }

    private static boolean hasPermission(String permission, RenderContext context, HumanEntity entity) {
        return permission == null || context.permissionEvaluator().has(entity, permission);
    }
}
