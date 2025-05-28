package com.github.imdmk.spenttime.user.feature.migration;

import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Factory responsible for creating instances of {@link MigrationTask}
 * with all required dependencies injected.
 */
public class MigrationTaskFactory {

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final Logger logger;
    private final MessageService messageService;
    private final UserService userService;
    private final MigrationManager migrationManager;

    /**
     * Constructs a {@code MigrationTaskFactory} with required dependencies.
     *
     * @param logger            the logger for logging migration operations
     * @param messageService    the service used for sending feedback to users
     * @param userService       the service responsible for managing user data
     * @param migrationManager  the manager handling task scheduling
     */
    public MigrationTaskFactory(
            @NotNull Logger logger,
            @NotNull MessageService messageService,
            @NotNull UserService userService,
            @NotNull MigrationManager migrationManager
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
        this.migrationManager = Objects.requireNonNull(migrationManager, "migrationManager cannot be null");
    }

    /**
     * Creates a {@link MigrationTask} with the default batch size.
     *
     * @param sender       the command sender initiating the migration
     * @param playerQueue  the list of players to be migrated
     * @return a new {@link MigrationTask} instance
     */
    public MigrationTask create(@NotNull CommandSender sender, @NotNull List<OfflinePlayer> playerQueue) {
        return this.create(sender, playerQueue, DEFAULT_BATCH_SIZE);
    }

    /**
     * Creates a {@link MigrationTask} with the specified batch size.
     *
     * @param sender       the command sender initiating the migration
     * @param playerQueue  the list of players to be migrated
     * @param batchSize    the number of players to process per batch
     * @return a new {@link MigrationTask} instance
     */
    public MigrationTask create(
            @NotNull CommandSender sender,
            @NotNull List<OfflinePlayer> playerQueue,
            int batchSize
    ) {
        Objects.requireNonNull(sender, "sender cannot be null");
        Objects.requireNonNull(playerQueue, "playerQueue cannot be null");

        return new MigrationTask(
                sender,
                this.logger,
                this.messageService,
                this.migrationManager,
                this.userService,
                playerQueue,
                batchSize
        );
    }
}
