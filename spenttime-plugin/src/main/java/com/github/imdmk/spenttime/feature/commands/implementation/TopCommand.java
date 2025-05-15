package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.feature.gui.GuiProvider;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.user.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command(name = "spenttime top")
@Permission("command.spenttime.top")
public class TopCommand {

    private final PluginConfiguration pluginConfiguration;
    private final UserRepository userRepository;
    private final MessageService messageService;

    public TopCommand(
            @NotNull PluginConfiguration pluginConfiguration,
            @NotNull UserRepository userRepository,
            @NotNull MessageService messageService
    ) {
        this.pluginConfiguration = pluginConfiguration;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Execute
    void showTop(@Context Player player) {
        this.userRepository.findTopUsersBySpentTime(this.pluginConfiguration.querySize)
                .thenAccept(topUsers -> {
                    if (topUsers.isEmpty()) {
                        this.messageService.create()
                                .viewer(player)
                                .notice(notice -> notice.topListEmpty)
                                .send();
                        return;
                    }

                    GuiProvider.openGui(SpentTimeTopGui.GUI_IDENTIFIER, player, topUsers);
                })
                .exceptionally(throwable -> {
                    this.messageService.create()
                            .viewer(player)
                            .notice(notice -> notice.topListQueryError)
                            .send();
                    throw new RuntimeException(throwable);
                });
    }
}
