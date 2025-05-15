package com.github.imdmk.spenttime.gui;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IdentifiableGui {

    @NotNull String getIdentifier();

}
