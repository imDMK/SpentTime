package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.feature.gui.GuiProvider;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGui;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGuiAction;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.repository.UserRepository;
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

@Command(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class ResetAllCommand {

    private static final long ZERO_TIME = 0L;

    private final Logger logger;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final BukkitSpentTime bukkitSpentTime;

    public ResetAllCommand(
            @NotNull Logger logger,
            @NotNull MessageService messageService,
            @NotNull UserRepository userRepository,
            @NotNull BukkitSpentTime bukkitSpentTime
    ) {
        this.logger = logger;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.bukkitSpentTime = bukkitSpentTime;
    }

    @Execute
    void resetAll(@Context CommandSender sender) {
        if (sender instanceof Player player) {
            this.openConfirmGui(player);
            return;
        }

        this.globalResetSpentTime(sender);
    }

    private void globalResetSpentTime(@NotNull CommandSender sender) {
        this.userRepository.resetGlobalSpentTime()
                .thenAcceptAsync(v -> {
                    this.bukkitSpentTime.resetAllSpentTime();
                    this.messageService.send(sender, notice -> notice.globalTimeReset);
                })
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while trying to reset global spent time", throwable);
                    this.messageService.send(sender, notice -> notice.globalTimeResetError);
                    return null;
                });
    }

    private void openConfirmGui(@NotNull Player viewer) {
        GuiProvider.openGui(
                ConfirmationGui.GUI_IDENTIFIER,
                viewer,
                ConfirmationGuiAction.builder()
                        .onConfirm(player -> {
                            this.globalResetSpentTime(player);
                            player.closeInventory();
                        })
                        .onCancel(HumanEntity::closeInventory)
                        .build()
        );
    }

}
