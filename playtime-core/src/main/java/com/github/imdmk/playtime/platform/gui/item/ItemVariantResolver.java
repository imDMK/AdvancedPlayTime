package com.github.imdmk.playtime.platform.gui.item;

import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public interface ItemVariantResolver {

    ItemGui resolve(@NotNull HumanEntity viewer,
                    @NotNull RenderContext context,
                    @NotNull Iterable<? extends ItemGui> candidates,
                    @NotNull ItemGui fallback);
}
