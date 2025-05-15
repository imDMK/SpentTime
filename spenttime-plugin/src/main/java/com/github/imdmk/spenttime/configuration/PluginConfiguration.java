package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@Header({
        "#",
        "# A plugin configuration file for the SpentTime plugin.",
        "#",
        "# If you have a problem with plugin configuration, please create an issue on the project's github.",
        "# However, if you like the plugin, leave a star for the project on GitHub.",
        "# ",
        "# Support site: https://github.com/imDMK/SpentTime/issues/new/choose",
        "# GitHub: https://github.com/imDMK/SpentTime",
        "#",
})
public class PluginConfiguration extends ConfigSection {

    @Comment("# Check for plugin update and send notification after administrator join to server?")
    public boolean checkUpdate = true;

    @Comment("# How often should the plugin check for updates? Recommended value: 1 day")
    public Duration updateInterval = Duration.ofDays(1);

    @Comment({
            "# Specifies how often the player's spent time should be saved in the database",
            "# Including a spent time top update",
            "# Recommended value: 10m"
    })
    public Duration spentTimeSaveDelay = Duration.ofMinutes(10L);

    @Comment({
            "# This determines how many players are to be displayed in the player top",
            "# WARNING: Increasing this value may increase the consumption of database server resources",
            "# Recommended value: 10"
    })
    public int querySize = 10;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new SerdesCommons());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "pluginConfiguration.yml";
    }
}
