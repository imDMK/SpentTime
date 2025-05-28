package com.github.imdmk.spenttime.user.feature.command;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.infrastructure.gui.GuiManager;
import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.user.feature.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime top")
@Permission("command.spenttime.top")
public class TopCommand {

    private final Logger logger;
    private final PluginConfiguration pluginConfiguration;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final GuiManager guiManager;

    public TopCommand(
            @NotNull Logger logger,
            @NotNull PluginConfiguration pluginConfiguration,
            @NotNull UserRepository userRepository,
            @NotNull MessageService messageService,
            @NotNull GuiManager guiManager
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.pluginConfiguration = Objects.requireNonNull(pluginConfiguration, "pluginConfiguration cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.guiManager = Objects.requireNonNull(guiManager, "guiManager cannot be null");
    }

    @Execute
    void showTop(@Context Player player) {
        this.userRepository.findTopUsersBySpentTime(this.pluginConfiguration.querySize)
                .thenAcceptAsync(topUsers -> {
                    if (topUsers.isEmpty()) {
                        this.messageService.create()
                                .viewer(player)
                                .notice(notice -> notice.topListEmpty)
                                .send();
                        return;
                    }

                    this.guiManager.openGui(SpentTimeTopGui.GUI_IDENTIFIER, player, topUsers);
                })
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while retrieving top list", throwable);
                    this.messageService.create()
                            .viewer(player)
                            .notice(notice -> notice.topListQueryError)
                            .send();
                    return null;
                });
    }
}
