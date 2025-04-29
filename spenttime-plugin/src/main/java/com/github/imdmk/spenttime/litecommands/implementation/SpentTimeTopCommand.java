package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "spenttime top")
@Permission("command.spenttime.top")
public class SpentTimeTopCommand {

    private final PluginConfiguration pluginConfiguration;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final com.github.imdmk.spenttime.user.gui.SpentTimeTopGui spentTimeTopGui;

    public SpentTimeTopCommand(PluginConfiguration pluginConfiguration, UserRepository userRepository, MessageService messageService, com.github.imdmk.spenttime.user.gui.SpentTimeTopGui spentTimeTopGui) {
        this.pluginConfiguration = pluginConfiguration;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.spentTimeTopGui = spentTimeTopGui;
    }

    @Execute
    void showTopSpentTime(@Context Player player) {
        this.userRepository.findTopUsersBySpentTime(this.pluginConfiguration.querySize)
                .thenAccept(topUsers -> {
                    if (topUsers.isEmpty()) {
                        this.messageService.create()
                                .viewer(player)
                                .notice(notice -> notice.topSpentTimeIsEmpty)
                                .send();
                        return;
                    }

                    if (this.spentTimeTopGui.getGuiConfig().type == GuiType.DISABLED) {
                        this.sendSpentTimeTop(player, topUsers);
                        return;
                    }

                    this.spentTimeTopGui.open(player, topUsers);
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .viewer(player)
                            .notice(notice -> notice.topSpentTimeQueryError)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }

    private void sendSpentTimeTop(Player player, List<User> topUsers) {
        this.messageService.create()
                .viewer(player)
                .notice(notice -> notice.topSpentTimeListFirst)
                .send();

        for (int i = 0; i < topUsers.size(); i++) {
            int position = i + 1;
            User user = topUsers.get(i);
            String userName = user.getName();
            String userSpentTime = DurationUtil.toHumanReadable(user.getSpentTimeAsDuration());

            this.messageService.create()
                    .viewer(player)
                    .notice(notice -> notice.topSpentTimeList)
                    .placeholder("{POSITION}", String.valueOf(position))
                    .placeholder("{PLAYER}", userName)
                    .placeholder("{TIME}", userSpentTime)
                    .send();
        }
    }
}
