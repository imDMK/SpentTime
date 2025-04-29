package com.github.imdmk.spenttime.litecommands.implementation.reset;

import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
public class SpentTimeResetCommand {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimeResetCommand(UserRepository userRepository, MessageService messageService, TaskScheduler taskScheduler, BukkitSpentTimeService bukkitSpentTimeService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.taskScheduler = taskScheduler;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }

    @Execute
    void resetSpentTimeTarget(@Context CommandSender sender, @Arg User target) {
        if (sender instanceof Player player) {
            this.showGui(player, target);
            return;
        }

        this.resetSpentTime(sender, target);
    }

    private void showGui(Player player, User target) {
        new ConfirmGui(this.taskScheduler)
                .create("<red>Reset " + target.getName() + " player spent time?")
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.resetSpentTime(player, target);
                })
                .closeAfterCancel()
                .open(player);
    }

    private void resetSpentTime(CommandSender sender, User target) {
        target.setSpentTime(0L);

        this.userRepository.save(target)
                .thenAcceptAsync(updatedUser -> {
                    this.bukkitSpentTimeService.resetSpentTime(target.getUuid());

                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.targetSpentTimeHasBeenReset)
                            .placeholder("{PLAYER}", target.getName())
                            .send();
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.targetSpentTimeResetError)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }
}
