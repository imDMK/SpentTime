package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime")
public class TimeCommand {

    private final Logger logger;
    private final UserService userService;
    private final MessageService messageService;
    private final BukkitSpentTime bukkitSpentTime;

    public TimeCommand(
            @NotNull Logger logger,
            @NotNull UserService userService,
            @NotNull MessageService messageService,
            @NotNull BukkitSpentTime bukkitSpentTime
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
    }

    @Execute
    @Permission("command.spenttime")
    void showTime(@Context Player player) {
        this.updateUser(player)
                .thenRun(() -> this.messageService.create()
                        .viewer(player)
                        .notice(notice -> notice.ownSpentTime)
                        .placeholder("{TIME}", this.getSpentTime(player.getUniqueId()))
                        .send());
    }

    @Execute
    @Permission("command.spenttime.target")
    void showTimeTarget(@Context CommandSender sender, @Arg Player target) {
        this.updateUser(target)
                .thenRun(() -> this.messageService.create()
                        .viewer(sender)
                        .notice(notice -> notice.otherPlayerSpentTime)
                        .placeholder("{PLAYER}", target.getName())
                        .placeholder("{TIME}", this.getSpentTime(target.getUniqueId()))
                        .send());
    }

    public CompletableFuture<Void> updateUser(@NotNull Player player) {
        return this.userService.findOrCreateUser(player)
                .thenCompose(user -> CompletableFuture.runAsync(() -> this.userService.updateUser(player, user)))
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while updating user", throwable);
                    return null;
                });
    }

    private @NotNull String getSpentTime(@NotNull UUID uuid) {
        return DurationUtil.format(this.bukkitSpentTime.getSpentTime(uuid));
    }

}
