package com.github.imdmk.spenttime.platform.gui.render;

/**
 * Defines how a GUI element should behave when the viewer lacks
 * the required permission to interact with or view the item.
 */
public enum NoPermissionPolicy {

    /**
     * The item is completely hidden and not placed in the GUI.
     */
    HIDE,

    /**
     * The item is still visible but interaction is disabled.
     * Clicking it will trigger the "onDenied" consumer if provided.
     */
    DISABLE
}
