package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigSection extends OkaeriConfig {

    public abstract @NotNull OkaeriSerdesPack getSerdesPack();

    public abstract @NotNull String getFileName();
}
