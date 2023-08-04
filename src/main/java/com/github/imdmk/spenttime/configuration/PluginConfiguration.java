package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class PluginConfiguration extends OkaeriConfig {

    public boolean checkForUpdate = true;

    @Comment({
            "# Specifies how often the player's spent time should be saved in the database",
            "# Including a spent time top update",
            "# Recommended value: 10m"
    })
    public Duration playerSpentTimeSaveDuration = Duration.ofMinutes(10L);

    public String spentTimeResetTimeCommandPermission = "spenttime.resettime";
    public String spentTimeResetTimeForAllCommandPermission = "spenttime.resettimeforall";

    public GuiConfiguration guiConfiguration = new GuiConfiguration();
    public MessageConfiguration messageConfiguration = new MessageConfiguration();
    public DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
}
