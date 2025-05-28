package com.github.imdmk.spenttime.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a GUI component that can be uniquely identified by a string identifier.
 * <p>
 * Useful for registering and retrieving GUI instances by their identifier.
 */
@FunctionalInterface
public interface IdentifiableGui {

    /**
     * Returns the unique identifier for this GUI.
     *
     * @return the non-null unique identifier string
     */
    @NotNull
    String getIdentifier();
}
