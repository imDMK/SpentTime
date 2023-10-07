package com.github.imdmk.spenttime.notification.configuration;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class NotificationSettings extends OkaeriConfig {

    @Comment("# {TIME} - The player spent time")
    public Notification playerSpentTime = new Notification(NotificationType.CHAT, "<gray>You have spent <red>{TIME} <gray>on the server<dark_gray>.");

    @Comment({
            "# {PLAYER} - Target player name",
            "# {TIME} - Target player spent time"
    })
    public Notification targetSpentTime = new Notification(NotificationType.CHAT, "<gray>Player <green>{PLAYER} has spent <red>{TIME} on the server<dark_gray>.");
    
    public Notification topSpentTimeIsEmpty = new Notification(NotificationType.CHAT, "<red>The top spent time is empty<dark_gray>.");

    @Comment("# Used when top spent time GUI is disabled")
    public Notification topSpentTimeListFirst = new Notification(NotificationType.CHAT, "<green>Top 10 players who spent the most time on the server<dark_gray>:");

    @Comment({
            "# Used when top spent time GUI is disabled",
            "# {POSITION} - The player position", 
            "# PLAYER} - The player name",
            "# {TIME} - The player spent time"
    })
    public Notification topSpentTimeList = new Notification(NotificationType.CHAT, "<dark_gray>- <green>{POSITION} Player {PLAYER}<dark_gray>: <red>{TIME}");

    @Comment({
            "# Spent time reset notifications",
            "# {PLAYER} - The player name"
    })
    public Notification targetSpentTimeHasBeenReset = new Notification(NotificationType.CHAT, "<red>The player {PLAYER} spent time on the server has been reset<dark_gray>.");
    public Notification globalSpentTimeHasBeenReset = new Notification(NotificationType.CHAT, "<green>Time spent on the server has been reset for all users.");

    @Comment("# {PERMISSIONS} - Required permissions")
    public Notification missingPermissions = new Notification(NotificationType.CHAT, "<red>Missing permissions: <dark_red>{PERMISSIONS}<dark_gray>.");

    public Notification playerNotFound = new Notification(NotificationType.CHAT, "<red>Player not found<dark_gray>.");

    @Comment("# {USAGE} - Correct usage of command")
    public Notification invalidUsage = new Notification(NotificationType.CHAT, "<red>Invalid usage: <dark_red>{USAGE}");

    @Comment("# Used when there is more than one option to use a command")
    public Notification invalidUsageFirst = new Notification(NotificationType.CHAT, "<red>Invalid usage:");

    @Comment({
            "# {USAGE} - Correct usage of command",
            "# Used when there is more than one option to use a command"
    })
    public Notification invalidUsageList = new Notification(NotificationType.CHAT, "<dark_gray>- <red>{USAGE}");
}
