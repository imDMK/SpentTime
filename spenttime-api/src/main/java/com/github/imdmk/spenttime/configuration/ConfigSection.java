package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

public abstract class ConfigSection extends OkaeriConfig {

    public abstract OkaeriSerdesPack getSerdesPack();

    public abstract String getFileName();
}
