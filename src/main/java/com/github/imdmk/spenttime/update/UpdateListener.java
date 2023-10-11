package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.github.imdmk.spenttime.configuration.implementation.PluginConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationType;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateListener implements Listener {

    private static final String PREFIX = "<dark_gray>[<rainbow>SpentTime<dark_gray>]";
    private static final Notification UPDATE_AVAILABLE = new Notification(NotificationType.CHAT, PREFIX + " <yellow>A new version is available: {TAG}\n<yellow>Download it here: {URL}");
    private static final Notification UPDATE_EXCEPTION = new Notification(NotificationType.CHAT, PREFIX + "<red>An error occurred while checking for update: {MESSAGE}");

    private final Logger logger;
    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;
    private final UpdateService updateService;
    private final TaskScheduler taskScheduler;

    public UpdateListener(Logger logger, PluginConfiguration pluginConfiguration, NotificationSender notificationSender, UpdateService updateService, TaskScheduler taskScheduler) {
        this.logger = logger;
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

            Formatter formatter = new Formatter()
                    .placeholder("{TAG}", latestRelease.getTag())
                    .placeholder("{URL}", latestRelease.getPageUrl());

            this.notificationSender.send(player, UPDATE_AVAILABLE, formatter);
        }
        catch (GitException gitException) {
            this.logger.log(Level.SEVERE, "An error occurred while checking for update", gitException);

            Formatter formatter = new Formatter()
                    .placeholder("{MESSAGE}", gitException.getMessage());

            this.notificationSender.send(player, UPDATE_EXCEPTION, formatter);
        }
    }
}
