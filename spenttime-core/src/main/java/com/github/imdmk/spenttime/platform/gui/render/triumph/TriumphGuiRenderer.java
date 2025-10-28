package com.github.imdmk.spenttime.platform.gui.render.triumph;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.platform.gui.item.ItemGui;
import com.github.imdmk.spenttime.platform.gui.item.ItemGuiTransformer;
import com.github.imdmk.spenttime.platform.gui.render.GuiRenderer;
import com.github.imdmk.spenttime.platform.gui.render.NoPermissionPolicy;
import com.github.imdmk.spenttime.platform.gui.render.RenderContext;
import com.github.imdmk.spenttime.platform.gui.render.RenderOptions;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * TriumphGUI-backed implementation of {@link GuiRenderer}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Evaluate permission via {@link RenderContext},</li>
 *   <li>Apply {@link NoPermissionPolicy}:</li>
 *   <ul>
 *     <li>{@code HIDE} → do not place the item,</li>
 *     <li>{@code DISABLE}/{@code SHOW_WITH_INFO} → place item; block clicks when disallowed.</li>
 *   </ul>
 *   <li>Delegate {@code ItemGui → GuiItem} conversion to {@link ItemGuiTransformer}.</li>
 * </ul>
 *
 * <strong>Thread-safety:</strong> Stateless; safe to reuse. All {@code put(...)} calls must occur on the Bukkit main thread.
 */
public final class TriumphGuiRenderer implements GuiRenderer {

    @Override
    @Contract(mutates = "param1")
    public void put(@NotNull BaseGui gui,
                    int slot,
                    @NotNull ItemGui item,
                    @NotNull RenderContext context,
                    @NotNull RenderOptions options,
                    @NotNull Consumer<InventoryClickEvent> onClick) {

        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(item, "item cannot be null");
        Validator.notNull(context, "context cannot be null");
        Validator.notNull(options, "options cannot be null");
        Validator.notNull(onClick, "onClick cannot be null");

        final boolean allowed = item.requiredPermission() == null
                || context.permissionEvaluator().has(context.viewer(), item.requiredPermission());

        // Do not place
        final NoPermissionPolicy policy = options.policy();
        if (!allowed && policy == NoPermissionPolicy.HIDE) {
            return;
        }

        final GuiAction<InventoryClickEvent> guardedClick = event -> {
            if (!allowed) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                options.onDenied().accept(event);
                return;
            }

            onClick.accept(event);
        };

        // Pure conversion
        final GuiItem guiItem = ItemGuiTransformer.toGuiItem(item, guardedClick);

        gui.setItem(slot, guiItem);
    }
}
