package com.github.imdmk.spenttime.update;

import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateController implements Listener {

    private static final String PREFIX = "<dark_gray>[<rainbow>SpentTime<dark_gray>]";

    private static final Notice UPDATE_AVAILABLE = Notice.chat(PREFIX + " <red><yellow>A new version is available: {TAG}\n<green>Download it here: {URL}");
    private static final Notice UPDATE_EXCEPTION = Notice.chat(PREFIX + "<red>An error occurred while checking for update: {MESSAGE}");

    private final Logger logger;
    private final PluginConfiguration pluginConfiguration;
    private final MessageService messageService;
    private final UpdateService updateService;
    private final TaskScheduler taskScheduler;

    public UpdateController(Logger logger, PluginConfiguration pluginConfiguration, MessageService messageService, UpdateService updateService, TaskScheduler taskScheduler) {
        this.logger = logger;
        this.pluginConfiguration = pluginConfiguration;
        this.messageService = messageService;
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

            this.messageService.create()
                    .viewer(player)
                    .notice(notice -> UPDATE_AVAILABLE)
                    .placeholder("{TAG}", String.valueOf(latestRelease.getTag()))
                    .placeholder("{URL}", latestRelease.getPageUrl())
                    .send();
        }
        catch (GitException gitException) {
            this.logger.log(Level.SEVERE, "An error occurred while checking for update", gitException);

            this.messageService.create()
                    .viewer(player)
                    .notice(notice -> UPDATE_EXCEPTION)
                    .placeholder("{MESSAGE}", gitException.getMessage())
                    .send();
        }
    }
}
