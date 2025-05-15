package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime set")
@Permission("command.spenttime.set")
public class SetCommand {

    private final Logger logger;
    private final UserService userService;
    private final MessageService messageService;
    private final BukkitSpentTime bukkitSpentTime;

    public SetCommand(
            @NotNull Logger logger,
            @NotNull UserService userService,
            @NotNull MessageService messageService,
            @NotNull BukkitSpentTime bukkitSpentTime
    ) {
        this.logger = logger;
        this.userService = userService;
        this.messageService = messageService;
        this.bukkitSpentTime = bukkitSpentTime;
    }

    @Execute
    void setTime(@Context CommandSender sender, @Arg User target, @Arg Duration time) {
        target.setSpentTime(time);

        this.userService.saveUser(target)
                .thenAcceptAsync(user -> {
                    this.bukkitSpentTime.setSpentTime(target.getUuid(), time);
                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.playerTimeSet)
                            .placeholder("{PLAYER}", target.getName())
                            .placeholder("{TIME}", DurationUtil.format(time))
                            .send();
                })
                .exceptionally(throwable -> {
                    this.messageService.send(sender, notice -> notice.playerTimeSetError);
                    this.logger.log(Level.SEVERE, "An error occurred while setting spent time for player", throwable);
                    return null;
                });
    }
}
