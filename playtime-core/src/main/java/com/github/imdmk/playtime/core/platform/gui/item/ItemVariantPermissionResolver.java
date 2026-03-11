package com.github.imdmk.playtime.core.platform.gui.item;

import com.github.imdmk.playtime.core.platform.gui.render.RenderContext;
import org.bukkit.entity.HumanEntity;

public final class ItemVariantPermissionResolver implements ItemVariantResolver {

    @Override
    public ItemGui resolve(
            HumanEntity viewer,
            RenderContext context,
            Iterable<? extends ItemGui> candidates,
            ItemGui fallback
    ) {
        for (ItemGui item : candidates) {
            if (item == null) {
                continue;
            }

            String permission = item.requiredPermission();
            if (permission == null || context.permissionEvaluator().has(viewer, permission)) {
                return item;
            }
        }

        return fallback;
    }
}
