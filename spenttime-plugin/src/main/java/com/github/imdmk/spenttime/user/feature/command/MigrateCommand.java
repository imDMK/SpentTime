package com.github.imdmk.spenttime.user.feature.command;

import com.github.imdmk.spenttime.infrastructure.BukkitSpentTime;
import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.user.feature.migration.MigrationManager;
import com.github.imdmk.spenttime.user.feature.migration.MigrationTask;
import com.github.imdmk.spenttime.user.feature.migration.MigrationTaskFactory;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Command(name = "spenttime migrate")
@Permission("command.spenttime.migrate")
public class MigrateCommand {

    private final MessageService messageService;
    private final BukkitSpentTime bukkitSpentTime;
    private final MigrationManager migrationManager;
    private final MigrationTaskFactory migrationTaskFactory;

    public MigrateCommand(
            @NotNull MessageService messageService,
            @NotNull BukkitSpentTime bukkitSpentTime,
            @NotNull MigrationManager migrationManager,
            @NotNull MigrationTaskFactory migrationTaskFactory
    ) {
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
        this.migrationManager = Objects.requireNonNull(migrationManager, "migrationManager cannot be null");
        this.migrationTaskFactory = Objects.requireNonNull(migrationTaskFactory, "migrationTaskFactory cannot be null");
    }

    @Execute
    void migrate(@Context CommandSender sender) {
        if (this.migrationManager.isScheduled()) {
            this.messageService.send(sender, notice -> notice.migrationAlreadyRunning);
            return;
        }

        MigrationTask migrationTask = this.migrationTaskFactory.create(sender, this.bukkitSpentTime.offlinePlayers());
        this.migrationManager.schedule(migrationTask);
    }

    @Execute(name = "stop")
    void migrateStop(@Context CommandSender sender) {
        if (!this.migrationManager.isScheduled()) {
            this.messageService.send(sender, notice -> notice.migrationNotRunning);
            return;
        }

        this.migrationManager.cancel();
        this.messageService.send(sender, notice -> notice.migrationCancelled);
    }
}
