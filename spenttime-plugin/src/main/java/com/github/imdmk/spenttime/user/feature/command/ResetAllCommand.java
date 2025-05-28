package com.github.imdmk.spenttime.user.feature.command;

import com.github.imdmk.spenttime.infrastructure.BukkitSpentTime;
import com.github.imdmk.spenttime.infrastructure.gui.GuiManager;
import com.github.imdmk.spenttime.infrastructure.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.infrastructure.gui.implementation.ConfirmGuiAction;
import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class ResetAllCommand {

    private final Logger logger;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final BukkitSpentTime bukkitSpentTime;
    private final GuiManager guiManager;

    public ResetAllCommand(
            @NotNull Logger logger,
            @NotNull MessageService messageService,
            @NotNull UserRepository userRepository,
            @NotNull BukkitSpentTime bukkitSpentTime,
            @NotNull GuiManager guiManager
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
        this.guiManager = Objects.requireNonNull(guiManager, "guiManager cannot be null");
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
        this.guiManager.openGui(
                ConfirmGui.GUI_IDENTIFIER,
                viewer,
                ConfirmGuiAction.builder()
                        .onConfirm(player -> {
                            this.globalResetSpentTime(player);
                            player.closeInventory();
                        })
                        .onCancel(HumanEntity::closeInventory)
                        .build()
        );
    }

}
