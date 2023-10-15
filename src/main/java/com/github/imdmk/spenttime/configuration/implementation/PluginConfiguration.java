package com.github.imdmk.spenttime.configuration.implementation;

import com.github.imdmk.spenttime.database.DatabaseSettings;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.gui.settings.ScrollingGuiSettings;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;

import java.time.Duration;

@Header({
        "#",
        "# Configuration file for the SpentTime plugin.",
        "#",
        "# If you have a problem with plugin configuration, please create an issue on the project's github.",
        "# However, if you like the plugin, leave a star for the project on GitHub.",
        "# ",
        "# Support site: https://github.com/imDMK/SpentTime/issues/new/choose",
        "# GitHub: https://github.com/imDMK/SpentTime",
        "#",
})
public class PluginConfiguration extends OkaeriConfig {

    @Comment("# Check for plugin update after the administrator join to server?")
    public boolean checkForUpdate = true;

    @Comment({
            "# Specifies how often the player's spent time should be saved in the database",
            "# Including a spent time top update",
            "# Recommended value: 10m"
    })
    public Duration spentTimeSaveDelay = Duration.ofMinutes(10L);

    @Comment({"#", "# Gui settings", "#"})
    public GuiSettings guiSettings = new GuiSettings();

    @Comment({"#", "# Scrolling gui settings", "#"})
    public ScrollingGuiSettings scrollingGuiSettings = new ScrollingGuiSettings();

    @Comment({"#", "# Database settings", "#"})
    public DatabaseSettings databaseSettings = new DatabaseSettings();

    @Comment({"#", "# Notification settings", "#"})
    public NotificationSettings notificationSettings = new NotificationSettings();
}
