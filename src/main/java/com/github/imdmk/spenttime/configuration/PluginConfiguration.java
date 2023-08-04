package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.OkaeriConfig;

public class PluginConfiguration extends OkaeriConfig {

    public boolean checkForUpdate = true;

    public String spentTimeResetTimeCommandPermission = "spenttime.resettime";
    public String spentTimeResetTimeForAllCommandPermission = "spenttime.resettimeforall";

    public GuiConfiguration guiConfiguration = new GuiConfiguration();
    public MessageConfiguration messageConfiguration = new MessageConfiguration();
    public DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
}
