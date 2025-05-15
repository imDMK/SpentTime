package com.github.imdmk.spenttime.feature.update;

import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.util.DurationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateController implements Listener {

    private static final String PREFIX = "<dark_gray>[<rainbow>AdvancedFurnaces<dark_gray>] ";
    private static final Notice UPDATE_AVAILABLE = Notice.chat(
            " ",
            PREFIX + "<rainbow>A new update is available!",
            "<dark_gray>- <rainbow>We strongly recommend downloading it!",
            " "
    );
    private static final Notice UPDATE_EXCEPTION = Notice.chat(
            " ",
            PREFIX + "<red>An error occurred while checking for plugin update! Next update check: {UPDATE_CHECK_INTERVAL}",
            " "
    );

    private final Logger logger;
    private final PluginConfiguration pluginConfiguration;
    private final MessageService messageService;
    private final UpdateService updateService;
    private final TaskScheduler taskScheduler;

    public UpdateController(Logger logger, PluginConfiguration pluginConfiguration, MessageService messageService, UpdateService updateService, TaskScheduler taskScheduler) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.pluginConfiguration = Objects.requireNonNull(pluginConfiguration, "pluginConfiguration cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.updateService = Objects.requireNonNull(updateService, "updateService cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.pluginConfiguration.checkUpdate) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (this.updateService.shouldCheck()) {
            this.taskScheduler.runAsync(() -> this.checkForUpdate(player));
        }
    }

    private void checkForUpdate(Player player) {
        try {
            GitCheckResult result = this.updateService.check();
            if (result.isUpToDate()) {
                return;
            }

            this.sendNotice(player, UPDATE_AVAILABLE);
        }
        catch (GitException exception) {
            this.logger.log(Level.SEVERE, "An error occurred while checking for update", exception);
            this.sendNotice(player, UPDATE_EXCEPTION);
        }
    }

    private void sendNotice(Player player, Notice notice) {
        this.messageService.create()
                .notice(notice)
                .placeholder("{UPDATE_CHECK_INTERVAL}", DurationUtil.format(this.pluginConfiguration.updateInterval))
                .viewer(player)
                .send();
    }
}
