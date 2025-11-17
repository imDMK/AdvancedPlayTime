package com.github.imdmk.spenttime.shared.gui;

/**
 * Defines the supported GUI layout types within the plugin.
 *
 * <p>Each type represents a different interaction model for displaying items.</p>
 */
public enum GuiType {

    /**
     * A fixed-size GUI without pagination or scrolling.
     * Suitable for simple static interfaces.
     */
    STANDARD,

    /**
     * A multipage GUI used for displaying large sets of items.
     * Provides navigation between pages.
     */
    PAGINATED,

    /**
     * A GUI that supports vertical scrolling.
     * Ideal for lists of items exceeding the visible height.
     */
    SCROLLING_VERTICAL,

    /**
     * A GUI that supports horizontal scrolling.
     * Useful for side-by-side item navigation.
     */
    SCROLLING_HORIZONTAL
}
