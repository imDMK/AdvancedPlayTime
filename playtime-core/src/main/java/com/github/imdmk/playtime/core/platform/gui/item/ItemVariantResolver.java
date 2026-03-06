package com.github.imdmk.playtime.core.platform.gui.item;

import com.github.imdmk.playtime.core.platform.gui.render.RenderContext;
import org.bukkit.entity.HumanEntity;


public interface ItemVariantResolver {

    ItemGui resolve(HumanEntity viewer,
                    RenderContext context,
                    Iterable<? extends ItemGui> candidates,
                    ItemGui fallback);
}
