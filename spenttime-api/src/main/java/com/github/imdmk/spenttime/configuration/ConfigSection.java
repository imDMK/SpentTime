package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for configuration sections.
 *
 * <p>
 * Extends {@link OkaeriConfig} to provide a reusable foundation for plugin
 * configuration sections. Subclasses are required to specify the
 * serialization/deserialization pack and the configuration file name.
 * </p>
 *
 * <p>
 * Supports automatic recursive loading of nested {@link ConfigSection}
 * subclasses declared as fields inside this class.
 * </p>
 */
public abstract class ConfigSection extends OkaeriConfig {

    /**
     * Returns the {@link OkaeriSerdesPack} instance used for serializing and deserializing
     * this configuration section.
     *
     * @return non-null serialization/deserialization pack
     */
    public abstract @NotNull OkaeriSerdesPack getSerdesPack();

    /**
     * Returns the filename (including extension) used to persist this configuration section.
     *
     * @return non-null configuration file name
     */
    public abstract @NotNull String getFileName();
}

