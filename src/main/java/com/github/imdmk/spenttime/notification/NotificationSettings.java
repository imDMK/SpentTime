package com.github.imdmk.spenttime.notification;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class NotificationSettings extends OkaeriConfig {

    @Comment("# {TIME} - The spent time")
    public Notification spentTimeNotification = new Notification(NotificationType.CHAT, "<gray>You have spent <red>{TIME} <gray>on the server<dark_gray>.");
    public Notification targetSpentTimeNotification = new Notification(NotificationType.CHAT, "<gray>Player <green>{PLAYER} has spent <red>{TIME} on the server<dark_gray>.");

    @Comment("# Spent time top notifications")
    public Notification topSpentTimeIsEmpty = new Notification(NotificationType.CHAT, "<red>The top spent time is empty<dark_gray>.");
    public Notification topSpentTimeListFirstNotification = new Notification(NotificationType.CHAT, "<green>Top 10 players who spent the most time on the server<dark_gray>:");
    @Comment("# {PLAYER} - The player name")
    public Notification topSpentTimeListNotification = new Notification(NotificationType.CHAT, "<dark_gray>- <green>{POSITION} Player {PLAYER}<dark_gray>: <red>{TIME}");

    @Comment({
            "# Spent time reset notifications",
            "# {PLAYER} - The player name"
    })
    public Notification targetResetSpentTimeNotification = new Notification(NotificationType.CHAT, "<red>The player {PLAYER} spent time on the server has been reset<dark_gray>.");
    public Notification resetGlobalSpentTimeNotification = new Notification(NotificationType.CHAT, "<green>Time spent on the server has been reset for all users.");

    @Comment({
            "# Error notifications",
            "# {PERMISSIONS} - Required permissions"
    })
    public Notification missingPermissionsNotification = new Notification(NotificationType.CHAT, "<red>Missing permissions: <dark_red>{PERMISSIONS}<dark_gray>.");
    public Notification playerNotFoundNotification = new Notification(NotificationType.CHAT, "<red>Player not found<dark_gray>.");

    @Comment({
            "# Invalid usage notifications",
            "# {USAGE} - Correct usage of command"
    })
    public Notification invalidUsageNotification = new Notification(NotificationType.CHAT, "<red>Invalid usage: <dark_red>{USAGE}");
    @Comment("# This messages is used when there is more than one option to use a command")
    public Notification invalidUsageFirstNotification = new Notification(NotificationType.CHAT, "<red>Invalid usage:");
    public Notification invalidUsageListNotification = new Notification(NotificationType.CHAT, "<dark_gray>- <red>{USAGE}");
}