package com.github.imdmk.spenttime.gui;

/**
 * Enum representing the different types of GUI layouts supported by the plugin.
 * <p>
 * - STANDARD: A fixed-size GUI without pagination or scrolling.
 * - PAGINATED: A GUI with multiple pages for handling many items.
 * - SCROLLING_VERTICAL: A GUI that supports vertical scrolling.
 * - SCROLLING_HORIZONTAL: A GUI that supports horizontal scrolling.
 */
public enum GuiType {
    STANDARD,
    PAGINATED,
    SCROLLING_VERTICAL,
    SCROLLING_HORIZONTAL
}
