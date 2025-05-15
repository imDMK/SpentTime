package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.feature.gui.GuiProvider;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGui;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGuiAction;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
public class ResetCommand {

    private final Logger logger;
    private final UserService userService;
    private final BukkitSpentTime bukkitSpentTime;
    private final MessageService messageService;

    public ResetCommand(
            @NotNull Logger logger,
            @NotNull UserService userService,
            @NotNull BukkitSpentTime bukkitSpentTime,
            @NotNull MessageService messageService
    ) {
        this.logger = logger;
        this.userService = userService;
        this.bukkitSpentTime = bukkitSpentTime;
        this.messageService = messageService;
    }

    @Async
    @Execute
    void resetTime(@Context CommandSender sender, @Arg User target) {
        if (sender instanceof Player player) {
            this.openConfirmGui(player, target);
            return;
        }

        this.resetSpentTime(sender, target);
    }

    private void resetSpentTime(@NotNull CommandSender sender, @NotNull User target) {
        target.setSpentTime(BukkitSpentTime.ZERO_SPENT_TIME);

        this.userService.saveUser(target)
                .thenAcceptAsync(user -> {
                    this.bukkitSpentTime.resetSpentTime(target.getUuid());
                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.playerTimeReset)
                            .placeholder("{PLAYER}", target.getName())
                            .send();
                })
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while trying to reset target's spent time", throwable);
                    this.messageService.send(sender, notice -> notice.playerTimeResetError);
                    return null;
                });
    }

    private void openConfirmGui(@NotNull Player viewer, @NotNull User target) {
        GuiProvider.openGui(
                ConfirmationGui.GUI_IDENTIFIER,
                viewer,
                ConfirmationGuiAction.builder()
                        .onConfirm(player -> {
                            this.resetSpentTime(player, target);
                            player.closeInventory();
                        })
                        .onCancel(HumanEntity::closeInventory)
                        .build()
        );
    }
}
