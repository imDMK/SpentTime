package com.github.imdmk.spenttime.user.feature.command;

import com.github.imdmk.spenttime.infrastructure.gui.GuiManager;
import com.github.imdmk.spenttime.infrastructure.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.infrastructure.gui.implementation.ConfirmGuiAction;
import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
public class ResetCommand {

    private final Logger logger;
    private final UserService userService;
    private final MessageService messageService;
    private final GuiManager guiManager;

    public ResetCommand(
            @NotNull Logger logger,
            @NotNull UserService userService,
            @NotNull MessageService messageService,
            @NotNull GuiManager guiManager
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.guiManager = Objects.requireNonNull(guiManager, "guiManager cannot be null");
    }

    @Execute
    void resetTime(@Context CommandSender sender, @Arg User target) {
        if (sender instanceof Player player) {
            this.openConfirmGui(player, target);
            return;
        }

        this.resetSpentTime(sender, target);
    }

    private void resetSpentTime(@NotNull CommandSender sender, @NotNull User target) {
        this.userService.setSpentTime(target, Duration.ZERO);

        this.userService.saveUser(target)
                .thenAccept(user -> this.messageService.create()
                        .viewer(sender)
                        .notice(notice -> notice.playerTimeReset)
                        .placeholder("{PLAYER}", target.getName())
                        .send()
                )
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while trying to reset target's spent time", throwable);
                    this.messageService.send(sender, notice -> notice.playerTimeResetError);
                    return null;
                });
    }

    private void openConfirmGui(@NotNull Player viewer, @NotNull User target) {
        this.guiManager.openGui(
                ConfirmGui.GUI_IDENTIFIER,
                viewer,
                ConfirmGuiAction.builder()
                        .onConfirm(player -> {
                            this.resetSpentTime(player, target);
                            player.closeInventory();
                        })
                        .onCancel(HumanEntity::closeInventory)
                        .build()
        );
    }
}
