package com.github.imdmk.spenttime.litecommands.implementation.reset;

import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class SpentTimeResetAllCommand {

    private final Server server;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimeResetAllCommand(Server server, UserRepository userRepository, MessageService messageService, TaskScheduler taskScheduler, BukkitSpentTimeService bukkitSpentTimeService) {
        this.server = server;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.taskScheduler = taskScheduler;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }

    @Execute
    void resetSpentTimeForAllPlayers(@Context CommandSender sender) {
        if (sender instanceof Player player) {
            this.showGui(player);
            return;
        }

        this.resetGlobalSpentTime(sender);
    }

    private void showGui(Player player) {
        new ConfirmGui(this.taskScheduler)
                .create("<red>Reset spent time of all users?")
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.resetGlobalSpentTime(player);
                })
                .closeAfterCancel()
                .open(player);
    }

    private void resetGlobalSpentTime(CommandSender sender) {
        this.userRepository.resetGlobalSpentTime()
                .thenAcceptAsync(v -> {
                    for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
                        this.bukkitSpentTimeService.resetSpentTime(offlinePlayer);
                    }

                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.globalSpentTimeHasBeenReset)
                            .send();
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.globalSpentTimeResetError)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }
}
