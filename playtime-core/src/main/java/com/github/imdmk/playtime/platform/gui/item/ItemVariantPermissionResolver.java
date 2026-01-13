package com.github.imdmk.playtime.platform.gui.item;

import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public final class ItemVariantPermissionResolver implements ItemVariantResolver {

    @Override
    public ItemGui resolve(
            @NotNull HumanEntity viewer,
            @NotNull RenderContext context,
            @NotNull Iterable<? extends ItemGui> candidates,
            @NotNull ItemGui fallback
    ) {
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
