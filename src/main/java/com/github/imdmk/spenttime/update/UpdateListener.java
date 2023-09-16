package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationType;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {

    private static final String DOUBLE_JUMP_PREFIX = "<dark_gray>[<red>DoubleJump<dark_gray>]";
    private static final String UPDATE_AVAILABLE = DOUBLE_JUMP_PREFIX + " <yellow>A new version is available: %s\n<yellow>Download it here: %s";
    private static final String UPDATE_EXCEPTION = DOUBLE_JUMP_PREFIX + "<red>An error occurred while checking for update: %s";

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;
    private final UpdateService updateService;
    private final TaskScheduler taskScheduler;

    public UpdateListener(PluginConfiguration pluginConfiguration, NotificationSender notificationSender, UpdateService updateService, TaskScheduler taskScheduler) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
        this.updateService = updateService;
        this.taskScheduler = taskScheduler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.pluginConfiguration.checkForUpdate) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        this.taskScheduler.runAsync(() -> this.checkForUpdate(player));
    }

    private void checkForUpdate(Player player) {
        try {
            GitCheckResult gitCheckResult = this.updateService.check();
            if (gitCheckResult.isUpToDate()) {
                return;
            }

            GitRelease latestRelease = gitCheckResult.getLatestRelease();

            String message = UPDATE_AVAILABLE.formatted(latestRelease.getTag(), latestRelease.getPageUrl());
            Notification updateAvailableNotification = new Notification(NotificationType.CHAT, message);

            this.notificationSender.send(player,updateAvailableNotification);
        }
        catch (GitException gitException) {
            String message = UPDATE_EXCEPTION.formatted(gitException.getMessage());
            Notification updateExceptionNotification = new Notification(NotificationType.CHAT, message);

            this.notificationSender.send(player, updateExceptionNotification);
        }
    }
}
