package com.github.imdmk.playtime.platform.gui.item;

import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves which {@link ItemGui} variant should be displayed to a viewer
 * based on their permission state.
 *
 * <p>This implementation iterates through candidate items in order and returns
 * the first one that either:
 * <ul>
 *   <li>Has no required permission ({@code requiredPermission() == null}), or</li>
 *   <li>Has a permission that the viewer possesses, as determined by
 *       {@link RenderContext#permissionEvaluator()}.</li>
 * </ul>
 * If no candidate matches, a predefined fallback item is returned.</p>
 *
 * <p><strong>Usage:</strong> Typically used by GUI renderers to determine which
 * item variant to display for users with different roles or permission levels.</p>
 *
 * <p><strong>Thread-safety:</strong> This resolver is stateless and thread-safe.</p>
 *
 * @see ItemGui
 * @see ItemVariantResolver
 * @see RenderContext
 */
public final class ItemVariantPermissionResolver implements ItemVariantResolver {

    /**
     * Resolves the first matching {@link ItemGui} variant visible to the given viewer.
     *
     * @param viewer    the player or entity viewing the GUI (non-null)
     * @param context   current rendering context, providing permission evaluation (non-null)
     * @param candidates ordered list of possible item variants (non-null)
     * @param fallback  default item to return if no candidate matches (non-null)
     * @return the first permitted item variant, or {@code fallback} if none are allowed
     * @throws NullPointerException if any argument is null
     */
    @Override
    public ItemGui resolve(
            @NotNull HumanEntity viewer,
            @NotNull RenderContext context,
            @NotNull Iterable<? extends ItemGui> candidates,
            @NotNull ItemGui fallback
    ) {
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(context, "context cannot be null");
        Validator.notNull(candidates, "candidates cannot be null");
        Validator.notNull(fallback, "fallback cannot be null");

        for (final ItemGui item : candidates) {
            if (item == null) {
                continue;
            }

            final String permission = item.requiredPermission();
            if (permission == null || context.permissionEvaluator().has(viewer, permission)) {
                return item;
            }
        }

        return fallback;
    }
}
