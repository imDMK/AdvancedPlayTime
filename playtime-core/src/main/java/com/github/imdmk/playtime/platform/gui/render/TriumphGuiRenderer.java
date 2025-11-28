package com.github.imdmk.playtime.platform.gui.render;

import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import com.github.imdmk.playtime.platform.gui.item.ItemGuiTransformer;
import com.github.imdmk.playtime.shared.Validator;
import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Default {@link GuiRenderer} implementation using the Triumph GUI API.
 * <p>
 * Responsible for rendering {@link ItemGui} objects into a {@link BaseGui},
 * applying permission policies and wiring click handlers.
 *
 * <p><b>Behavior:</b>
 * <ul>
 *     <li>If {@link NoPermissionPolicy#HIDE} → item is not rendered (returns {@code null}).</li>
 *     <li>If {@link NoPermissionPolicy#DISABLE} → click is blocked (cancelled) silently.</li>
 *     <li>Otherwise → executes provided click handler.</li>
 * </ul>
 */
public final class TriumphGuiRenderer implements GuiRenderer {

    /**
     * Creates a new renderer instance.
     * <p>Renderer is stateless and may be safely reused.</p>
     *
     * @return new {@link TriumphGuiRenderer} instance
     */
    public static TriumphGuiRenderer newRenderer() {
        return new TriumphGuiRenderer();
    }

    /**
     * Places a rendered {@link ItemGui} into the specified GUI slot.
     * <p>If the item should be hidden (policy {@code HIDE}), it will not be placed.</p>
     *
     * @param gui           target GUI
     * @param slot          target slot index
     * @param item          GUI item definition
     * @param context       render context
     * @param options       render options
     * @param onClick       click action to execute if allowed
     * @param builderEditor optional builder customization
     */
    @Override
    @Contract(mutates = "param1")
    public void setItem(
            @NotNull final BaseGui gui,
            final int slot,
            @NotNull final ItemGui item,
            @NotNull final RenderContext context,
            @NotNull final RenderOptions options,
            @NotNull final Consumer<InventoryClickEvent> onClick,
            @NotNull final Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        validateArgs(gui, item, context, options, onClick, builderEditor);

        final GuiItem guiItem = buildGuiItem(item, context, options, onClick, builderEditor);
        if (guiItem != null) {
            gui.setItem(slot, guiItem);
        }
    }

    /**
     * Adds a rendered {@link ItemGui} to the GUI at the next available position.
     * <p>If the item should be hidden (policy {@code HIDE}), it will not be added.</p>
     *
     * @param gui           target GUI
     * @param item          GUI item definition
     * @param context       render context
     * @param options       render options
     * @param onClick       click action to execute if allowed
     * @param builderEditor optional builder customization
     */
    @Override
    @Contract(mutates = "param1")
    public void addItem(
            @NotNull final BaseGui gui,
            @NotNull final ItemGui item,
            @NotNull final RenderContext context,
            @NotNull final RenderOptions options,
            @NotNull final Consumer<InventoryClickEvent> onClick,
            @NotNull final Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        validateArgs(gui, item, context, options, onClick, builderEditor);

        final GuiItem guiItem = buildGuiItem(item, context, options, onClick, builderEditor);
        if (guiItem != null) {
            gui.addItem(guiItem);
        }
    }

    /**
     * Builds a {@link GuiItem} based on the given item definition and context.
     * <p>
     * Permission logic:
     * <ul>
     *     <li>If the viewer lacks permission and policy is {@code HIDE}, returns {@code null}.</li>
     *     <li>If the viewer lacks permission and policy is {@code DISABLE}, click is blocked silently.</li>
     * </ul>
     *
     * @return a built {@link GuiItem}, or {@code null} if hidden
     */
    private @Nullable GuiItem buildGuiItem(
            @NotNull final ItemGui item,
            @NotNull final RenderContext context,
            @NotNull final RenderOptions options,
            @NotNull final Consumer<InventoryClickEvent> onClick,
            @NotNull final Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        final String requiredPerm = item.requiredPermission();

        final GuiAction<InventoryClickEvent> clickHandler = event -> {
            if (!has(requiredPerm, context, event.getWhoClicked())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                options.onDenied().accept(event);
                return;
            }

            onClick.accept(event);
        };

        final boolean allowedForViewerNow = has(requiredPerm, context, context.viewer());
        if (!allowedForViewerNow && options.policy() == NoPermissionPolicy.HIDE) {
            return null;
        }

        return ItemGuiTransformer.toGuiItem(item, clickHandler, builderEditor);
    }

    /**
     * Checks if the given entity has the required permission.
     *
     * @param permission permission string or {@code null}
     * @param context    render context
     * @param entity     entity to check
     * @return {@code true} if allowed, otherwise {@code false}
     */
    private static boolean has(@Nullable final String permission,
                               @NotNull final RenderContext context,
                               @NotNull final HumanEntity entity) {
        return permission == null || context.permissionEvaluator().has(entity, permission);
    }

    /**
     * Ensures all arguments are non-null.
     *
     * @throws NullPointerException if any argument is {@code null}
     */
    private static void validateArgs(
            final BaseGui gui,
            final ItemGui item,
            final RenderContext context,
            final RenderOptions options,
            final Consumer<InventoryClickEvent> onClick,
            final Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(item, "item cannot be null");
        Validator.notNull(context, "context cannot be null");
        Validator.notNull(options, "options cannot be null");
        Validator.notNull(onClick, "onClick cannot be null");
        Validator.notNull(builderEditor, "builderEditor cannot be null");
    }
}
