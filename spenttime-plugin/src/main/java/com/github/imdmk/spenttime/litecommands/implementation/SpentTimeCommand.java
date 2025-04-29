package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Command(name = "spenttime")
public class SpentTimeCommand {

    private final MessageService messageService;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimeCommand(MessageService messageService, BukkitSpentTimeService bukkitSpentTimeService) {
        this.messageService = messageService;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }


    @Execute
    @Permission("command.spenttime")
    void showSpentTime(@Context Player player) {
        String playerSpentTime = DurationUtil.toHumanReadable(this.bukkitSpentTimeService.getSpentTime(player));

        this.messageService.create()
                .viewer(player)
                .notice(notice -> notice.playerSpentTime)
                .placeholder("{TIME}", playerSpentTime)
                .send();
    }

    @Async
    @Execute
    @Permission("command.spenttime.target")
    void showTarget(@Context CommandSender sender, @Arg User target) {
        String targetSpentTime = DurationUtil.toHumanReadable(this.updateSpentTime(target));

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.targetSpentTime)
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", targetSpentTime)
                .send();
    }

    private Duration updateSpentTime(User target) {
        Duration spentTime = this.bukkitSpentTimeService.getSpentTime(target.getUuid());
        target.setSpentTime(spentTime);
        return spentTime;
    }
}
