package com.github.imdmk.spenttime.feature.commands.implementation;

import com.github.imdmk.spenttime.configuration.ConfigurationManager;
import com.github.imdmk.spenttime.feature.message.MessageService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "spenttime reload")
@Permission("command.spenttime.reload")
public class ReloadCommand {

    private final Logger logger;
    private final ConfigurationManager configurationManager;
    private final MessageService messageService;

    public ReloadCommand(
            @NotNull Logger logger,
            @NotNull ConfigurationManager configurationManager,
            @NotNull MessageService messageService
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.configurationManager = Objects.requireNonNull(configurationManager, "configurationManager cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
    }

    @Execute
    void reload(@Context CommandSender sender) {
        this.configurationManager.reloadAll()
                .thenAccept(v -> this.messageService.send(sender, notice -> notice.reload))
                .exceptionally(throwable -> {
                    this.messageService.send(sender, notice -> notice.reloadError);
                    this.logger.log(Level.SEVERE, "Failed to reload plugin configuration.", throwable);
                    return null;
                });
    }
}
