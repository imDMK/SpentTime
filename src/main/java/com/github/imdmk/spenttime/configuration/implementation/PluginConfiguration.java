package com.github.imdmk.spenttime.configuration.implementation;

import com.github.imdmk.spenttime.command.configuration.CommandConfiguration;
import com.github.imdmk.spenttime.database.DatabaseConfiguration;
import com.github.imdmk.spenttime.gui.GuiConfiguration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class PluginConfiguration extends OkaeriConfig {

    @Comment("# Check for plugin update after the administrator join to server?")
    public boolean checkForUpdate = true;

    @Comment({
            "# Specifies how often the player's spent time should be saved in the database",
            "# Including a spent time top update",
            "# Recommended value: 10m"
    })
    public Duration playerSpentTimeSaveDuration = Duration.ofMinutes(10L);

    public CommandConfiguration commandConfiguration = new CommandConfiguration();
    public GuiConfiguration guiConfiguration = new GuiConfiguration();
    public MessageConfiguration messageConfiguration = new MessageConfiguration();
    public DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
}
