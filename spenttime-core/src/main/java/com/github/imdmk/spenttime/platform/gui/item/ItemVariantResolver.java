package com.github.imdmk.spenttime.platform.gui.item;

import com.github.imdmk.spenttime.platform.gui.render.RenderContext;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a strategy for selecting which {@link ItemGui} variant
 * should be displayed to a specific viewer during GUI rendering.
 *
 * <p>Implementations of this interface encapsulate different
 * resolution logics — e.g., by permission, by user state,
 * by contextual conditions, or by custom business rules.</p>
 *
 * <p>The resolver is typically used within GUI frameworks to decide
 * which visual representation of an item (variant) to render for a given player.</p>
 *
 * <p><strong>Thread-safety:</strong> Implementations should be stateless and thread-safe.</p>
 *
 * @see ItemGui
 * @see RenderContext
 * @see ItemVariantPermissionResolver
 */
public interface ItemVariantResolver {

    /**
     * Resolves the most appropriate {@link ItemGui} variant to display.
     *
     * @param viewer     the player or entity viewing the GUI (non-null)
     * @param context    the current rendering context providing permission checks, locale, etc. (non-null)
     * @param candidates iterable collection of possible item variants, evaluated in order (non-null)
     * @param fallback   default item variant to use if none match (non-null)
     * @return the resolved item variant, never {@code null} (at least {@code fallback})
     * @throws NullPointerException if any parameter is null
     */
    ItemGui resolve(@NotNull HumanEntity viewer,
                    @NotNull RenderContext context,
                    @NotNull Iterable<? extends ItemGui> candidates,
                    @NotNull ItemGui fallback);
}
