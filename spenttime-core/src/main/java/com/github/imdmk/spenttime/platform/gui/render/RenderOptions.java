package com.github.imdmk.spenttime.platform.gui.render;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Rendering options that define how permission handling
 * and denied interactions are processed during GUI rendering.
 *
 * @param policy   how to handle items when the viewer lacks permission
 * @param onDenied consumer called when a denied item is clicked
 * <p>
 * <strong>Thread-safety:</strong> This record is immutable and thread-safe,
 * provided that the supplied {@link Consumer} implementation is thread-safe.
 */
public record RenderOptions(
        @NotNull NoPermissionPolicy policy,
        @NotNull Consumer<InventoryClickEvent> onDenied
) {

    /**
     * Creates a default option that disables unauthorized items silently.
     *
     * @return the default {@link RenderOptions} instance
     */
    public static @NotNull RenderOptions defaultDenySilently() {
        return new RenderOptions(NoPermissionPolicy.DISABLE, e -> {});
    }

    /**
     * Creates a default option that hides unauthorized items completely.
     *
     * @return the default {@link RenderOptions} instance
     */
    public static @NotNull RenderOptions defaultHide() {
        return new RenderOptions(NoPermissionPolicy.HIDE, e -> {});
    }
}
