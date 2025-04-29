package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Command(name = "spenttime set")
@Permission("command.spenttime.set")
public class SpentTimeSetCommand {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimeSetCommand(UserRepository userRepository, MessageService messageService, BukkitSpentTimeService bukkitSpentTimeService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }

    @Execute
    void setTime(@Context CommandSender sender, @Arg User target, @Arg Duration time) {
        target.setSpentTime(time);

        this.userRepository.save(target)
                .thenAcceptAsync(updatedUser -> {
                    this.bukkitSpentTimeService.setSpentTime(target.getUuid(), time);

                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.targetSpentTimeHasBeenSet)
                            .placeholder("{PLAYER}", target.getName())
                            .placeholder("{TIME}", DurationUtil.toHumanReadable(time))
                            .send();
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .viewer(sender)
                            .notice(notice -> notice.targetSpentTimeSetError)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }
}
